# Demo: PyRPC Framework

本项目是 https://github.com/PerryYao-GitHub/demo-rpc 的代码整理与升级, 主要修改与新增如下:

- 允许开发者配置使用的信息交互协议 (自定义TCP / HTTP)
- 增加了无注册中心的选项
- 增加了注册中心的配置功能, 可在配置文件中配置注册中心
- 修改了代码结构, 各个组件之间的功能逻辑更加清晰

## Intro

- 基于 Java + Vertx + Etcd + 自定义协议实现
- 允许开发者引入相对应的 Spring Boot Starter, 通过注解和配置文件快速使用框架, 像调用本地服务一样轻松调用远程服务
- 支持 SPI 机制. 提供了默认的注册中心, 序列化器, 负载均衡器, 重试器和容错器几种实现, 也允许开发者自行动态实现

## Tech Stack

- Java Reflection
- SPI 机制
- Web Server
- 自定义通信协议 (TCP)
- Vertx
- ETCD
- 负载均衡

## Main Components

:star:**消费者服务代理 (`com.ypy.pyrpc.proxy.ServiceProxy`)**

为Consumer提供代理服务对象, 让Consumer像调用本地方法一样调用代理服务对象的方法

:star:**网络服务器 (`com.ypy.pyrpc.server.RpcServer`)** 

在Provider中需要开启网络服务器, 以处理来自Consumer的RPC请求, 在Consumer中不需要它

2种实现类 (HTTP, 自定义TCP), 使用工厂模式, 可以依据开发者的配置文件使用不同的实现类

- 基于 Vertx 的 HTTP 服务器
- 自定义的高性能网络通信协议 (TCP)
  - `com.ypy.pyrpc.server.tcp.TcpBufferHandlerWrapper`解决数据传输过程中的粘包问题

:star:**网络服务器客户端 (`com.ypy.pyrpc.server.RpcClient`)**

在`com.ypy.pyrpc.proxy.ServiceProxy`中被调用, 帮助Consumer发送请求

与网络服务器的设计类似, 2种实现类 (HTTP, 自定义TCP),  使用工厂模式, 可以依据开发者的配置文件使用不同的实现类

:star:**请求处理器 (`com.ypy.pyrpc.server.tcp.TcpServerHandler`, `com.ypy.pyrpc.server.http.HttpServerHandler`)**

解析收到的RPC请求, 根据请求里面的服务名称, 方法名称, 以及提供的参数及参数类型, 在本地服务信息注册器中找到对应的服务实现类的Class对象, 并使用`invoke()`调用相应的方法, 获取结果, 并把结果响应回Consumer端

:star:**本地服务信息注册器 (`com.ypy.pyrpc.app.RpcLocalRegistry`)**

使用线程安全的 `ConcurrentHashMap` 存储Provider本地提供的服务实名称 (`String`) 和服务实现类的Class对象 (`Class<?>`)

通过`Class<?> getServiceImpl(String serviceName)`方法获取服务实现类的Class对象, 以便被`invoke()`调用以获取服务方法的结果

:star:**SPI 组件 (`com.ypy.pyrpc.spi.SpiLoader`)**

使用两个`ConcurrentHashMap`存贮功能接口和实现类的信息, 并引入缓存机制:

- `private static final Map<String, Map<String, Class<?>>> spiMap` : `{interfaceName: {implClassKey: implClass}}`, 例如 `{"Registry" : {"jdk": Class<JdkRegistry>, "json": Class<JsonRegistry>}}`
- `private static final Map<String, Object> implClassInstanceCache`: `{implClassName: implClassInstance}`, 例如 `{"JdkRegistry": JdkRegistry}`. 作为对象缓存, 避免重复new对象

使用SPI机制, 为以下四个功能模块提供了可定制化选择: 1) 注册中心; 2) 序列化器; 3) 负载均衡器; 4) 重试和容错器. 其中:

:stars:**注册中心 (`com.ypy.pyrpc.spi.registry.Registry`)**

注册中心接口有六个方法, 分别是注册中心初始化, 服务注册, 服务注销, 服务发现, 节点关闭, 心跳检测, 以及节点监视. 

提供了 ETCD 的默认实现 (`com.ypy.pyrpc.spi.registry.impl.EtcdRegistry`), 以及无注册中心的配置选项. 

:stars:**序列化器 (`com.ypy.pyrpc.spi.serializer.Serializer`)**

提供了 JDK, JSON, KRYO 三种序列化器的默认实现

:stars:**负载均衡器 (`com.ypy.pyrpc.spi.loadbalancer.Loadbalancer`)**

使用轮询, 随机, 和一致哈希等方法实现了默认的三种序列化器实现

:stars:**重试和容错器 (`com.ypy.pyrpc.spi.retry.Retry`, `com.ypy.pyrpc.spi.tolerance.Tolerance`)**

这一部分只做了简单的实现和占位, 开发者可以使用SPI机制自定义实现

:star:**RPC 配置, 启动等全局组件 (`com.ypy.pyrpc.app.RpcApplication`, `com.ypy.pyrpc.config`)**

本框架的配置信息可以写入`resources/application.properties`, 完整的配置信息如下:

```
pyrpc.serverHost=127.0.0.1
pyrpc.serverPort=8081
pyrpc.serverType=http || tcp
pyrpc.mock=false || true
pyrpc.registry.registry=no || etcd
pyrpc.registry.addr=http://127.0.0.1:2375
pyrpc.registry.username=xxx
pyrpc.registry.password=yyy
pyrpc.registry.timeout=10000
pyrpc.serializer=jdk || json || kryo
pyrpc.loadbalancer=round-robin || random || consistent-hash
pyrpc.retry=no || fixed-interval
pyrpc.tolerance=fail-fast || fail-safe
```

*\*其中 || 分割了可供选择的默认实现类的 key*

在 `resources/META-INF/prrpc/` 中, 有 `custom/` 和 `system/` 两个目录, 其中 `system/` 中的文件如下:

```
com.ypy.pyrpc.spi.loadbalancer.Loadbalancer
com.ypy.pyrpc.spi.registry.Registry
com.ypy.pyrpc.spi.retry.Retry
com.ypy.pyrpc.spi.serializer.Serializer
com.ypy.pyrpc.spi.tolerance.Tolerance
```

分别对应了提供 SPI 自定义功能实现类的接口名, 在相对应的文件内, 可以写入功能实现类以及其对应的 key

例如框架中的默认实现类:

```
no=com.ypy.pyrpc.spi.registry.impl.NoRegistry
etcd=com.ypy.pyrpc.spi.registry.impl.EtcdRegistry
```

