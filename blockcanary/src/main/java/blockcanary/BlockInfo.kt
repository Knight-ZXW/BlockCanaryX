package blockcanary

import com.knightboost.stacksampler.StackTraceSample

class BlockInfo {
    var message: String? = null

    var startTime: Long = 0

    var endTime: Long = 0

    var dispatchFinish = false

    var sampleInterval:Int = 50

    var stackTraceSamples = mutableListOf<StackTraceSample>()

    fun costTime(): Long {
        return endTime - startTime
    }
}