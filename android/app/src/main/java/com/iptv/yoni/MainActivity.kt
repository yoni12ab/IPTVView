package com.iptv.yoni

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    // ── Replace with your GitHub Pages URL after deploying ──────────────────
    private val APP_URL = "https://yoni12ab.github.io/IPTVView/"
    // ────────────────────────────────────────────────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on + immersive fullscreen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setImmersive()

        setContentView(R.layout.activity_main)

        webView            = findViewById(R.id.webview)
        fullscreenContainer = findViewById(R.id.fullscreen_container)

        webView.settings.apply {
            javaScriptEnabled                = true
            domStorageEnabled               = true   // localStorage → session persistence
            mediaPlaybackRequiresUserGesture = false  // autoplay
            mixedContentMode                = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort                 = true
            loadWithOverviewMode            = true
            builtInZoomControls             = false
            displayZoomControls             = false
            allowFileAccess                 = true
        }

        // Fullscreen video support (triggered by video.requestFullscreen() in JS)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (customView != null) { callback.onCustomViewHidden(); return }
                customView         = view
                customViewCallback = callback
                fullscreenContainer.addView(view)
                fullscreenContainer.visibility = View.VISIBLE
                webView.visibility             = View.GONE
                setImmersive()
            }

            override fun onHideCustomView() {
                fullscreenContainer.visibility = View.GONE
                fullscreenContainer.removeAllViews()
                webView.visibility = View.VISIBLE
                customView         = null
                customViewCallback = null
                setImmersive()
            }
        }

        webView.webViewClient = WebViewClient()
        webView.loadUrl(APP_URL)
    }

    // Back button: exit fullscreen first, then let WebView handle history
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (customView != null) {
                customViewCallback?.onCustomViewHidden()
                return true
            }
            if (webView.canGoBack()) {
                webView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        setImmersive()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    @Suppress("DEPRECATION")
    private fun setImmersive() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN          or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY    or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN   or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )
    }
}
