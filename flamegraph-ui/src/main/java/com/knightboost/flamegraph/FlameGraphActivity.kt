package com.knightboost.flamegraph

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Base64
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URLEncoder

class FlameGraphActivity : AppCompatActivity() {

    companion object {
        public fun start(context: Context, flameGraphPath: String, sampleInterval: Int) {
            val intent = Intent(context, FlameGraphActivity::class.java);
            intent.putExtra("flameGraphPath", flameGraphPath)
            intent.putExtra("sampleInterval", sampleInterval)
            context.startActivity(intent)
        }
    }

    private val flameGraphFilePath by lazy {
        return@lazy intent.extras?.getString("flameGraphPath")
    }

    private val sampleInterval by lazy {
        return@lazy intent.extras?.getInt("sampleInterval")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flame_graph)
        val path = flameGraphFilePath
        var readText = File(path).readText()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasNecessaryPermission()) {
                val permissions = arrayOf(Manifest.permission.INTERNET)
                requestPermissions(permissions, 42)
            }
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        initWebView()
    }

    private fun initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        };

        var webView = findViewById<WebView>(R.id.webview)
        var settings = webView.settings
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN //自适应屏幕
        settings.loadWithOverviewMode = true //自适应屏幕
        settings.setSupportZoom(true)
        settings.useWideViewPort = true //扩大比例的缩放
        settings.builtInZoomControls = true //设置是否出现缩放工具
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.javaScriptEnabled = true
        webView.addJavascriptInterface(this, "NativeCall")
        webView.webViewClient = object : WebViewClient() {

        }
        try { //本地HTML里面有跨域的请求 原生webview需要设置之后才能实现跨域请求
            if (Build.VERSION.SDK_INT >= 16) {
                val clazz: Class<*> = webView.getSettings()::class.java
                val method: Method? = clazz.getMethod(
                    "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType
                )
                if (method != null) {
                    method.invoke(webView.getSettings(), true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        webView.loadUrl("file:android_asset/flame-graph.html?interval=" + sampleInterval)
    }

    @JavascriptInterface
    fun loadFlameGraph(): String {
        return File(flameGraphFilePath).readText()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasNecessaryPermission(): Boolean {
        return checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasNecessaryPermission().not()){
                Toast.makeText(application,"需要网络相关权限", Toast.LENGTH_LONG)
                    .show()
            }
        }
        finish()
    }

}