package com.iumansam.sigsticknoads

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.widget.LinearLayout
import android.widget.TextView

class CatalogActivity : Activity() {
    private var webView: WebView? = null
    private val bg = Color.rgb(12, 12, 19)
    private val purple = Color.rgb(167, 139, 250)
    private val white = Color.rgb(247, 245, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val title = intent.getStringExtra("title") ?: "Catálogo"
        val url = intent.getStringExtra("url") ?: "https://www.sigstick.com/"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 14, 20, 14)
        }

        val back = TextView(this).apply {
            text = "‹"
            textSize = 34f
            setTextColor(purple)
            gravity = Gravity.CENTER
            setPadding(0, 0, 20, 0)
            setOnClickListener { finish() }
        }

        val heading = TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }

        val external = TextView(this).apply {
            text = "↗"
            textSize = 25f
            setTextColor(purple)
            setPadding(12, 4, 4, 4)
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        header.addView(back)
        header.addView(heading)
        header.addView(external)
        root.addView(header)

        val web = WebView(this)
        webView = web
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = false
        web.settings.allowContentAccess = false
        web.settings.mixedContentMode =
            android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val target = request.url
                if (target.scheme != "https" && target.scheme != "http") {
                    return true
                }
                return false
            }
        }
        web.webChromeClient = WebChromeClient()
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        web.loadUrl(url)
    }

    override fun onBackPressed() {
        val web = webView
        if (web != null && web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView?.apply {
            stopLoading()
            destroy()
        }
        webView = null
        super.onDestroy()
    }
}
