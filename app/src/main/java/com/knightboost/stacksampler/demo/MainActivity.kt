package com.knightboost.stacksampler.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import java.io.File

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

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // 权限授予，执行需要权限的操作
            onNotificationPermissionGranted()
        } else {
            // 权限被拒绝，处理权限被拒绝的情况
            onNotificationPermissionDenied()
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
                message.arg1 = 500
                message.sendToTarget()
            }

        findViewById<View>(R.id.btn_block_test2)
            .setOnClickListener {
                BlockMethodMock.ioWork()
            }

        // 检查并请求通知权限
        checkAndRequestNotificationPermission()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // 权限已授予，执行需要权限的操作
                onNotificationPermissionGranted()
            }
        }
    }

    private fun onNotificationPermissionGranted() {
    }

    private fun onNotificationPermissionDenied() {
        Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show()
    }


}