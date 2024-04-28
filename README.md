 <p align=center><img src="https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202312181036449.jpg#pic_center" style="width: 40%;" /></p>
<p align="center">
<a>
    <img src="https://img.shields.io/badge/Spring Boot-2.6.13-brightgreen.svg" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Java-11-blue.svg" alt="Java">
    <img src="https://img.shields.io/badge/Etcd-3.5.12-yellow.svg" alt="Etcd">
    <img src="https://img.shields.io/badge/Zookeeper-3.8.4-red.svg" alt="Zookeeper">
    <img src="https://img.shields.io/badge/Hutool-5.8.16-green.svg" alt="Hutool">
    <img src="https://img.shields.io/badge/Maven-3.8.8-orange.svg" alt="Maven">
    <img src="https://img.shields.io/badge/Vert.x-4.5.1-blue.svg" alt="Vert.x">
</a>
</p>

# 一款轻量级自定义协议RPC框架 

## 项目介绍

基于 Java + Etcd + Vert.x + 自定义协议实现，开发者只需引入 Spring Boot Starter，就能通过注解和配置的方式快速使用框架，实现像调用本地方法一样轻松调用远程服务。

## 项目功能

- 基于`Vert.x`的TCP协议实现长连接通信，包括心跳检测、解决粘包半包等
- 基于`Etcd`实现分布式服务注册与发现，服务支持自动续期与下线注销
- 实现了轮询、随机、加权随机等负载均衡算法
- 支持`fastJson`、`hessian`、`kryo`、`jdk`的序列化方式
- 支持远程调用请求重试与容错机制

## 项目亮点

- 使用`SPI`机制方便支持开发者自行扩展注册中心、负载均衡器、重试策略、容错策略

- 通过修改配置文件更换序列化方式、注册中心

- 支持注解驱动，降低开发者使用成本

- 自定义`RPC`协议，提升网络传输性能

- 使用本地对象维护已获取到的服务提供者节点缓存，减轻注册中心压力。

## 项目流程
### 传统RPC流程

![image-20240329173629919](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403291736990.png)

### 项目框图

![image-20240329205625040](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403292056122.png)

- **config 配置层**：相关的配置

- **proxy 服务代理层**：负责对底层调用细节的封装，调用远程方法像调用本地的方法一样；
- **registry 注册中心层**：封装服务地址的注册与发现，以及一些权重，配置动态调整等功能；
- **cluster 路由层**：负责封装多个提供者的路由及负载均衡，并桥接注册中心；
- **protocol 协议层**：负责请求数据的转码封装等作用；
- **serialize 数据序列化层**：对需要在网络传输的数据进行序列化;
- **tolerant 容错层**：当服务调用出现失败之后需要有容错层的兜底辅助；

### 自定义协议结构

![img](https://cdn.jsdelivr.net/gh/0oHo0/Picture@main/img/202403301604038.png)

## 项目结构
目录