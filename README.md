

# BlockCananry [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.knight-zxw/blockcanary/badge.svg?style=flat)](https://github.com/Knight-ZXW/BlockCanaryX)

该库通过监听主线程Looper的消息处理时间，并通过stackSampler定时采样主线程的堆栈，当检测
到主线程Looper消息慢处理时，会通过`Notification`的方式告知用户，当用户点击卡顿详情时，
会跳转到一个Web页面，以火焰图的形式展示消息处理时间内的函数运行情况

# 项目说明
1.本项目的初衷是为了介绍 https://juejin.cn/post/7107137302043820039 这篇文章的内容，这个项目我基本上是利用周末一天的时间完成的，因此有很多不完善的地方，比如 消息没有支持清除的功能，因为本人平时工作时间较忙，另外正在准备开发一个开源的APM平台，因此不能保证这个项目的一些功能性的建议能够立刻完善(BUG除外)，烦请见谅。 如果你有兴趣继续优化这个项目可以直接Fork这个项目，提交MR。

# 功能界面
<p align="center">
<img src="/imgs/blocking_list.jpeg" width='260' height='500'>
<img src="/imgs/blocking_flamegraph.jpeg" width='260' height='500'>
<img src="/imgs/notification.jpeg" width='260' height='500'>
</p>



# 引入
```gradle
dependencies {
    //引入卡顿监控实现依赖库
    implementation 'io.github.knight-zxw:blockcanary:${latestVersion}'
    //引入卡顿消息通知及相关展示UI
    implementation 'io.github.knight-zxw:blockcanary-ui:${latestVersion}'

    // 如果你只想在debug包引入，不希望被引入release包，可以使用 debugImplementation
    //debugImplementation 'io.github.knight-zxw:blockcanary:${latestVersion}'
}
```

# 使用方法
默认情况下，blockcanary 基于androidx 的startup框架，会自动进行初始化。
如果需要个性化配置，则需要通过 在res/values 下配置资源值的方式 关闭自动化初始化
```
<item name="block_canary_auto_install" type="bool">false</item>
```

手动初始化的示例代码
```java
// 一般在 application onCreate阶段配置
val blockCanaryConfig = BlockCanaryConfig.newBuilder().build()
BlockCanary.install(application,blockCanaryConfig)
```
# 待支持功能/优化
- [ ] 卡顿消息支持一键清除、过期自动清除
- [ ] 记录消息处理消耗的cpu时间
- [ ] 记录慢消息执行时，所在的前台Activity, APP的状态、
