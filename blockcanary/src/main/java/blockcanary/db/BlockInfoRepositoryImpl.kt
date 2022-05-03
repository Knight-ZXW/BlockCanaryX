package blockcanary.db

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import blockcanary.BlockInfo
import java.io.File
import java.io.FilenameFilter
import java.util.concurrent.ExecutorService

class BlockInfoRepositoryImpl(
    private val mContext: Context,
    executors: ExecutorService,
    cacheDir: File
) : BlockInfoRepository {
    private val mSp: SharedPreferences
    private val executors: ExecutorService
    private val mGson = Gson()
    private val cacheDir: File

    /**
     *
     */
    override fun insertBlockInfo(blockInfo: BlockInfo) {
        executors.submit {
//            var blockMetaInfo = BlockMetaInfo(blockInfo.startTime, blockInfo.endTime)
//            var blocks: String = mSp.getString("blocks-meta", "")!!;
//            var blockMetaInfos: List<BlockMetaInfo> = mutableListOf();
//            if (blocks.length > 0) {
//                var blockMetaInfos: List<BlockMetaInfo> = mGson.fromJson<List<BlockMetaInfo>>(
//                    blocks,
//                    object : TypeToken<List<BlockMetaInfo>>() {}.rawType
//                )
//            }

            val text = mGson.toJson(blockInfo)
            val file = File(cacheDir, blockInfo.startTime.toString() + "-" + blockInfo.costTime() + ".blockInfo")
            if (cacheDir.exists().not()) {
                cacheDir.mkdirs()
            }
            if (file.exists().not()) {
                file.createNewFile()
            }
            file.writeText(text)
        }
    }

    override fun listBlockInfo(): List<BlockInfo> {
        val files = cacheDir.listFiles() ?: return emptyList()
        val blockInfoList = mutableListOf<BlockInfo>()
        for (file in files) {
            val name = file.nameWithoutExtension
            val splitTokens = name.split("-")
            val time = splitTokens[0].toLong()
            val costTime = splitTokens[1].toLong()
            val blockInfo = BlockInfo()
            blockInfo.startTime = time
            blockInfo.endTime = time + costTime
            blockInfoList.add(blockInfo)
        }
        blockInfoList.sortByDescending(BlockInfo::startTime)
        return blockInfoList
    }

    override fun blockInfoDetail(token: Long): BlockInfo? {
        val files = cacheDir.listFiles(object : FilenameFilter {
            override fun accept(dir: File, name: String): Boolean {
                return name.startsWith("${token}-")
            }
        }) ?: return null

        if (files.isEmpty()) {
            return null
        }

        val file = files[0]
        val text = file.readText()
        return try {
            val blockInfo = mGson.fromJson<BlockInfo>(text, BlockInfo::class.java)
            blockInfo
        } catch (e: Exception) {
            null
        }
    }

    override fun deleteAll() {
        val listFiles = cacheDir.listFiles() ?: return
        for (file in listFiles) {
            file.delete()
        }
    }

    override fun deleteBefore(token: Long) {
        val files = cacheDir.listFiles(object : FilenameFilter {
            override fun accept(dir: File, name: String): Boolean {
                val splitTokens = name.split("-")
                val time = splitTokens[0].toLong()
                if (time < token) {
                    return true
                }
                return false
            }
        }) ?: return
        for (file in files) {
            file.delete()
        }
    }

    override fun delete(token: Long) {
        val files = cacheDir.listFiles(object : FilenameFilter {
            override fun accept(dir: File, name: String): Boolean {
                val splitTokens = name.split("-")
                val time = splitTokens[0].toLong()
                if (time == token) {
                    return true
                }
                return false
            }
        }) ?: return
        for (file in files) {
            file.delete()
        }
    }

    init {
        mSp = mContext.getSharedPreferences("block_info_meta", Context.MODE_PRIVATE)
        this.executors = executors
        this.cacheDir = cacheDir
    }
}