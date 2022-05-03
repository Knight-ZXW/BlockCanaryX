package blockcanary

import java.util.concurrent.Executors

class SlowMessageNotifyHandler :SlowMessageHandler{

    private val executors = Executors.newSingleThreadExecutor()

    override fun onSlowMessageDetect(blockInfo: BlockInfo) {
        executors.submit{
            var dispatchStartTime = blockInfo.startTime
            val dispatchEndTime = blockInfo.endTime

        }

    }
}