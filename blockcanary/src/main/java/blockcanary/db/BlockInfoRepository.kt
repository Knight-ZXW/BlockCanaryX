package blockcanary.db

import blockcanary.BlockInfo

interface BlockInfoRepository {
    fun insertBlockInfo(blockInfo: BlockInfo)

    fun listBlockInfo(): List<BlockInfo>

    fun blockInfoDetail(timeToken: Long): BlockInfo?

    fun deleteBefore(timeToken: Long)

    fun delete(timeToken: Long)

    fun deleteAll()
}