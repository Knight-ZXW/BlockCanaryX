package com.knightboost.stacksampler.demo

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.annotation.NonNull

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "StackSampler"
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            var millSeconds = msg.arg1
            Thread.sleep(millSeconds.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    @NonNull
    private fun init() {
        findViewById<View>(R.id.btn_block_test)
            .setOnClickListener {
                var msg1 = handler.obtainMessage()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    msg1.isAsynchronous = true
                }
                var message = handler.obtainMessage()
                message.what = 301
                message.arg1= 500
                message.sendToTarget()


            }

        findViewById<View>(R.id.btn_block_test2)
            .setOnClickListener {
                                BlockMethodMock.ioWork()
            }
    }

}