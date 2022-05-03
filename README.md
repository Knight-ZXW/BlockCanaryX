
# BlockCananry [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.knight-zxw/blockcanary/badge.svg?style=flat)](https://github.com/Knight-ZXW/BlockCanaryX)

该库通过监听主线程Looper的消息处理时间，并通过stackSampler定时采样主线程的堆栈，当检测
到主线程Looper消息慢处理时，会通过`Notification`的方式告知用户，当用户点击卡顿详情时，
会跳转到一个Web页面，以火焰图的形式展示消息处理时间内的函数运行情况

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

    // 只想在在debug包
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
# TODO
[] 记录消息处理消耗的cpu时间
[] 记录慢消息执行时，前台Activity, APP的状态、