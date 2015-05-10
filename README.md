# push
推送服务，使用开源的gopush框架，基于akka实现的高性能推送服务。ios会推送到apns，wp推送到microsoft的推送服务器上，支持分布式

##特性

* 1、轻量级，完全依赖akka
* 2、高性能
* 3、纯scala实现，部分方法使用java
* 4、支持设置过期机制（用于gopush推送）
* 5、支持client消息发送失败重传机制，（消息缓存在 Guava Cache中）
* 6、支持worker到gopush的重传机制
* 7、可以方便的集成多种推送服务厂商
