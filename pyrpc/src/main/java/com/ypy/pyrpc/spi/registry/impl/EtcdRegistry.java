package com.ypy.pyrpc.spi.registry.impl;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.registry.Registry;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * etcd --listen-client-urls http://localhost:2375 --advertise-client-urls http://localhost:2375
 * start etcd service
 */
public class EtcdRegistry implements Registry {
    private Client cli;

    private KV kvCli;

    private static final String ETCD_ROOT_PATH = "/pyrpc/";

    /**
     * fullRegistryServiceKey: /pyrpc/com.....UserService:1.0.0/http://127.0.0.1:8080
     * <p>
     * this collection stores data for PROVIDER
     */
    private final Set<String> loaclFullRegistryServiceKeySet = new HashSet<String>();

    /**
     * key is serviceNameVersion
     * <p>
     * this collection stores data for CONSUMER, make convenience through cache ServiceNameVer and their MetaInfo
     */
    private final Map<String, List<ServiceMetaInfo>> serviceNameVerCache = new ConcurrentHashMap<>();

    private final Set<String> watchingNodeKeySet = new HashSet<>();

    @Override
    public void init(RpcConfig.RegistryConfig registryConfig) {
        cli = Client.builder()
                .endpoints(registryConfig.getAddr())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvCli = cli.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseCli = cli.getLeaseClient();

        long leaseId = leaseCli.grant(30L).get().getID();

        String fullRegistryServiceKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNameVerAddr();
        ByteSequence k = ByteSequence.from(fullRegistryServiceKey, StandardCharsets.UTF_8);
        ByteSequence v = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvCli.put(k, v, putOption).get();
        loaclFullRegistryServiceKeySet.add(fullRegistryServiceKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String fullRegistryServiceKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNameVerAddr();
        kvCli.delete(ByteSequence.from(fullRegistryServiceKey, StandardCharsets.UTF_8));
        loaclFullRegistryServiceKeySet.remove(serviceMetaInfo.getServiceNameVerAddr());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceNameVer) {
        List<ServiceMetaInfo> cachedServiceMetaInfos = serviceNameVerCache.get(serviceNameVer);
        if (cachedServiceMetaInfos != null) return cachedServiceMetaInfos;

        // search in etcd
        String searchPrefix = ETCD_ROOT_PATH + serviceNameVer + "/"; // add "/" means search for group
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvCli.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption
            ).get().getKvs();

            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream()
                    .map(kv -> {
                        String fullRegistryServiceKey = kv.getKey().toString(StandardCharsets.UTF_8);
                        watch(fullRegistryServiceKey, serviceNameVer);
                        String val = kv.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(val, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

            serviceNameVerCache.put(serviceNameVer, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("Get service list failed", e);
        }
    }

    @Override
    public void destroy() {
        for (String key : loaclFullRegistryServiceKeySet) {
            try {
                kvCli.delete(ByteSequence.from(key, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException(key + ": delete etcd node failed", e);
            }
        }
        if (kvCli != null) kvCli.close();
        if (cli != null) cli.close();

        System.out.println("Destroy Current Node");
    }

    @Override
    public void heartBeat() {
        // every 10 seconds, update lease for all services provided by current Provider
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : loaclFullRegistryServiceKeySet) {
                    try {
                        List<KeyValue> kvs = kvCli.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        if (kvs.isEmpty()) continue;
                        KeyValue kv = kvs.get(0);
                        String val = kv.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(val, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + ": updating lease failed", e);
                    }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String fullRegistryServiceKey, String serviceNameVer) {
        Watch watchCli = cli.getWatchClient();
        if (watchingNodeKeySet.add(fullRegistryServiceKey)) {
            watchCli.watch(ByteSequence.from(fullRegistryServiceKey, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            serviceNameVerCache.remove(serviceNameVer);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
