package com.yessfish.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable fullscreen mode AFTER setContentView
        enableFullscreenMode()

        setupWebView()
        loadYessFish()
        checkForUpdates()
    }

    /**
     * Enable fullscreen immersive mode
     * Hides system UI (status bar, navigation bar)
     */
    private fun enableFullscreenMode() {
        // For edge-to-edge experience
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) and above
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Android 10 and below
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }

        // Keep screen on while app is active
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Check for app updates
     */
    private fun checkForUpdates() {
        lifecycleScope.launch {
            try {
                val updateChecker = UpdateChecker(this@MainActivity)
                val versionInfo = updateChecker.checkForUpdates()

                if (versionInfo != null && updateChecker.isUpdateAvailable(versionInfo)) {
                    updateChecker.showUpdateDialog(versionInfo) {
                        updateChecker.downloadAndInstallUpdate(versionInfo)
                    }
                }
            } catch (e: Exception) {
                Log.e("YessFish", "Update check failed: ${e.message}")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webView)

        // WebView Settings
        webView.settings.apply {
            // JavaScript
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true

            // DOM Storage
            domStorageEnabled = true
            databaseEnabled = true

            // Cache
            cacheMode = WebSettings.LOAD_DEFAULT
            // Note: setAppCacheEnabled() is deprecated and removed in API 33+

            // Media
            mediaPlaybackRequiresUserGesture = false

            // Zoom
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            // Rendering
            useWideViewPort = true
            loadWithOverviewMode = true

            // Mixed content (allow HTTPS to load)
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

            // Geolocation
            setGeolocationEnabled(true)

            // File access
            allowFileAccess = true
            allowContentAccess = true
        }

        // Enable safe browsing (disabled for debug builds to prevent login issues)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
            // Only enable for release builds
            val isDebug = BuildConfig.DEBUG
            WebSettingsCompat.setSafeBrowsingEnabled(webView.settings, !isDebug)
        }

        // WebViewClient with custom headers
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                // Google OAuth URLs moeten in Chrome Custom Tabs openen (niet in WebView)
                if (url.contains("accounts.google.com") ||
                    url.contains("google.com/o/oauth2") ||
                    url.contains("googleapis.com/oauth")) {
                    // Open Google login in Chrome Custom Tab
                    openInCustomTab(url)
                    return true
                }

                // Only handle yessfish.com URLs in the app
                if (url.contains("yessfish.com")) {
                    view?.loadUrl(url, getCustomHeaders())
                    return true
                }

                // External URLs open in browser
                return false
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                // Add custom headers to ALL requests to yessfish.com
                if (request?.url?.host?.contains("yessfish.com") == true) {
                    val headers = request.requestHeaders.toMutableMap()
                    headers.putAll(getCustomHeaders())
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Page loaded successfully
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    // Show error page
                    showErrorPage()
                }
            }
        }

        // WebChromeClient for advanced features
        webView.webChromeClient = object : WebChromeClient() {

            // Geolocation permission
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                if (checkLocationPermission()) {
                    callback?.invoke(origin, true, false)
                } else {
                    requestLocationPermission()
                }
            }

            // File chooser for photo uploads
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                // Handle file selection (camera/gallery)
                if (checkCameraPermission()) {
                    // Show file chooser
                    return true
                } else {
                    requestCameraPermission()
                    return false
                }
            }

            // Progress bar
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                // Update progress indicator if needed
            }

            // Console messages for debugging
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    android.util.Log.d("WebView", "${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                }
                return true
            }
        }
    }

    /**
     * Get custom headers with app version
     * This tells the server we're the Android native app
     */
    private fun getCustomHeaders(): Map<String, String> {
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }

        return mapOf(
            BuildConfig.APP_VERSION_HEADER to versionName,
            "User-Agent" to "YessFish-Android/$versionName",
            "X-App-Platform" to "Android",
            "X-App-Version" to versionName,
            "X-YessFish-Android-App" to "true"
        )
    }

    private fun loadYessFish() {
        // Start at root - server will detect Android headers and route appropriately
        // User logs in first, then server can redirect to fishmap-android.php
        val url = BuildConfig.BASE_URL
        webView.loadUrl(url, getCustomHeaders())
    }

    /**
     * Open URL in Chrome Custom Tab (for Google OAuth)
     * Dit lost de "403 disallowed_useragent" error op
     */
    private fun openInCustomTab(url: String) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback: open in external browser
            android.util.Log.e("YessFish", "Failed to open Custom Tab: ${e.message}")
            Toast.makeText(this, "Opening in browser...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorPage() {
        val errorHtml = """
            <html>
            <body style="font-family: sans-serif; padding: 20px; text-align: center;">
                <h2>⚠️ Verbinding mislukt</h2>
                <p>Kon geen verbinding maken met YessFish.</p>
                <p>Controleer je internetverbinding.</p>
                <button onclick="location.reload()">Opnieuw proberen</button>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null)
    }

    // Permission handling
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Reload to enable location
                    webView.reload()
                } else {
                    Toast.makeText(this, "Locatie toegang geweigerd", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                } else {
                    Toast.makeText(this, "Camera toegang geweigerd", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Back button handling
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        // Re-enable fullscreen mode when app resumes
        enableFullscreenMode()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-enable fullscreen when window gains focus
            enableFullscreenMode()
        }
    }
}
