package blockcanary.ui

class BlockMetaInfo(
    private var startTime: Long,
    private var endTime: Long
) {
    fun costTime(): Long {
        return endTime - startTime
    }
}