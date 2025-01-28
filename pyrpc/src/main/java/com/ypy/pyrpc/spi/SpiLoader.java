package com.ypy.pyrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    /**
     * store the class that has been load, {interfaceName: {implClassKey: implClass}}
     * className means full name, e.g. com.ypy.pyrpc.spi.serializer.Serializer
     * classKey means a short key, representing some implClass, e.g. jdk=JdkSerializer
     */
    private static final Map<String, Map<String, Class<?>>> spiMap = new ConcurrentHashMap<>();

    /**
     * cache of instance, avoid repeat "new"
     * {implClassName: implClassInstance}
     * full-name, not a key
     */
    private static final Map<String, Object> implClassInstanceCache = new ConcurrentHashMap<String, Object>();
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/pyrpc/system/";
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/pyrpc/custom/";
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    public static Map<String, Class<?>> load(Class<?> interfaceClass) {
        String interfaceName = interfaceClass.getName();
        Map<String, Class<?>> implKey2implClassMap = new HashMap<>(); // {implClassKey: implClass}
        for (String dir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(dir + interfaceName);
            for (URL resource : resources) {
                try {
                    InputStreamReader isr = new InputStreamReader(resource.openStream());
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length == 2) {
                            String implClassKey = split[0];
                            String implClassName = split[1];
                            implKey2implClassMap.put(implClassKey, Class.forName(implClassName));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }
        spiMap.put(interfaceName, implKey2implClassMap);
        return implKey2implClassMap;
    }

    public static <T> T getInstance(Class<T> interfaceClass, String implClassKey) {
        String interfaceName = interfaceClass.getName();
        Map<String, Class<?>> implKey2implClassMap = spiMap.get(interfaceName);
        if (implKey2implClassMap == null) throw new RuntimeException("Interface " + interfaceName + " has no implement classes");
        if (!implKey2implClassMap.containsKey(implClassKey)) throw new RuntimeException("Interface " + interfaceName + " has no implement " + implClassKey);

        Class<?> implClass = implKey2implClassMap.get(implClassKey);
        if (!implClassInstanceCache.containsKey(implClass.getName())) {
            try {
                implClassInstanceCache.put(implClass.getName(), implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Write " + implClass.getName() + " into cache failed", e);
            }
        }
        return (T) implClassInstanceCache.get(implClass.getName());
    }
}
