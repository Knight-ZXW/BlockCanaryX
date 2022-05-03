package blockcanary

interface BlockDetectListener {
    fun onBlockDetected(blockInfo: BlockInfo)
}