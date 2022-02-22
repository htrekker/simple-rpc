# simple-rpc
An POC(prove of concept) rpc framework. Which support asynchronous communication，spring integration and load balance support.

## 技术选型

1. Netty
2. Kryo
3. Zookeeper

## 执行流程

1. 服务消费者获本地调用栈（stub）
2. client stub以method call的方式调用方法，stub封装本地调用方法的参数和通信等细节；
3. client stub通过服务发现获得服务列表，并通过负载均衡对服务的提供者进行选择；
4. client stub发送服务请求；
5. server singlton接受请求并解码，通过消费者传来的信息从容器中获取对应的实例；
6. server singleton**异步执行**，在完成后将结果（或异常信息）返回给消费者；
7. client stub解码并返回结果（或抛出异常）。

![img](https://github.com/zaiyunduan123/Java-Summarize/raw/master/image/rpc-1.jpg)

在这个过程中，主要使用了JDK动态代理、线程池、spring的listener等技术完成的实现。

## coming features

- [ ] Consistent hashing load balancer support
- [ ] Service breaker/downgrade support
- [ ] Protostuff Serializer support
- [ ] Auto-configuration Support

