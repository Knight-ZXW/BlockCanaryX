package blockcanary.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import blockcanary.ui.util.SimpleListAdapter
import blockcanary.ui.util.TimeFormatter
import blockcanary.*
import com.knightboost.flamegraph.FlameGraphActivity
import com.knightboost.stacksampler.util.FlameGraphUtil
import java.io.File
import java.lang.ref.WeakReference

class BlockListActivity : AppCompatActivity() {

    companion object{
        fun createPendingIntent(context: Context):PendingIntent{
            val intent = Intent(context,BlockListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            val flags = if (Build.VERSION.SDK_INT >= 23) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getActivity(context,
                1, intent, flags)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    fun refreshList() {
        Thread {
            val blocks = BlockCanary.blockInfoRepository.listBlockInfo()
            mainHandler.post { onBlocksRetrieved(blocks) }
        }.start()
    }

    fun onBlocksRetrieved(blocks: List<BlockInfo>) {
        val listView = findViewById<ListView>(R.id.block_canary_list)
        listView.adapter =
            SimpleListAdapter(R.layout.block_canary_block_row, blocks) { view, position ->
                val descriptionView = view.findViewById<TextView>(R.id.block_canary_block_text)
                val timeView = view.findViewById<TextView>(R.id.block_canary_time_text)

                val block = blocks[position]
                val context = view.context
                descriptionView.text = "消息处理耗时${block.costTime()}ms"

                val formattedDate =
                    TimeFormatter.formatTimestamp(context, block.startTime)
                timeView.text =
                    resources.getString(R.string.block_canary_group_list_time_label, formattedDate)

                view.setOnClickListener {
                    val contextRef = WeakReference<Context>(context)
                    CanaryExecutors.workExecutor.execute {
                        val blockDetail = BlockCanary.blockInfoRepository
                            .blockInfoDetail(block.startTime)
                        if (blockDetail == null) {
                            mainHandler.post {
                                Toast.makeText(
                                    context,
                                    "file is deleted or broken", Toast.LENGTH_SHORT
                                )
                            }
                            return@execute
                        }

                        val stackTraceSamples = blockDetail.stackTraceSamples
                        val flameGraphText = FlameGraphUtil.toFlameGraphText(stackTraceSamples, false)

                        //todo 临时进行文件存储
                        //todo 删除过期文件

                        val dir = File(context.cacheDir, "/flameGraph");
                        //clear old Files
                        dir.delete()

                        val file = File(dir, "${SystemClock.uptimeMillis()}.flameGraph")
                        file.parentFile?.mkdirs()
                        file.createNewFile()
                        file.writeText(flameGraphText)
                        FlameGraphActivity.start(
                            context,
                            file.absolutePath, 50
                        )
                    }

                }

                view.setOnLongClickListener {
                    onBlockLongClick(it, block)
                    return@setOnLongClickListener true
                }
            }
    }

    fun onBlockLongClick(view: View, blockInfo: BlockInfo) {
        val popupMenu = PopupMenu(this, view, Gravity.RIGHT or Gravity.BOTTOM)
        popupMenu.menuInflater.inflate(R.menu.block_item_action, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                BlockCanary.blockInfoRepository.delete(blockInfo.startTime)
                val blocks = BlockCanary.blockInfoRepository.listBlockInfo()
                mainHandler.post { onBlocksRetrieved(blocks) }
                return true
            }
        })
        popupMenu.show()
    }
}