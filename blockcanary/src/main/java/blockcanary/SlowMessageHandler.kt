package blockcanary

import android.util.Log

interface SlowMessageHandler {
    public fun onSlowMessageDetect(blockInfo: BlockInfo);
}


open class SlowMessageLogger: SlowMessageHandler {
    override fun onSlowMessageDetect(blockInfo: BlockInfo) {
        val msg  = "detect slowMessage delivery cost : ${blockInfo.costTime()} ms \n," +
                "detail message info: ${blockInfo}"
        Log.e("SlowMessageCanary",msg)

    }

}