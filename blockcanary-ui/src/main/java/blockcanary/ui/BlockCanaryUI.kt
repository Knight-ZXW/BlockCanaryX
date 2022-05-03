package blockcanary.ui

import blockcanary.*

object BlockCanaryUI : BlockDetectListener {

    private val application =BlockCanary.applicationContext()

    init {
        BlockCanary.addBlockDetectListener(this)
    }

    override fun onBlockDetected(blockInfo: BlockInfo) {
        val intent = BlockListActivity.createPendingIntent(application)
        Notifications.showNotification(application,
        "检测到慢消息处理","处理耗时: "+blockInfo.costTime()+" ms",intent,
            R.id.block_canary_blocking_detected,
            NotificationType.LEAKCANARY_MAX)
    }

}