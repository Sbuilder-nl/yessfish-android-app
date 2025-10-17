package com.yessfish.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
    private var isOAuthInProgress = false  // Track when OAuth login is active

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable fullscreen mode AFTER setContentView
        enableFullscreenMode()

        setupWebView()

        // Enable persistent cookies (stay logged in) - AFTER webView is initialized
        enablePersistentCookies()

        // Check if opened via custom URL scheme (OAuth callback)
        handleIntent(intent)

        loadYessFish()
        checkForUpdates()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.scheme == "yessfish") {

            when (data.host) {
                "oauth-success" -> {
                    // OAuth succeeded! Get login token from URL
                    val token = data.getQueryParameter("token")

                    if (token != null) {
                        // Exchange token for session in WebView
                        android.util.Log.d("YessFish", "Got OAuth token: ${token.substring(0, 8)}...")

                        Toast.makeText(this, "✅ Inloggen gelukt!", Toast.LENGTH_SHORT).show()

                        // Wait a moment for Custom Tab to close
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            // First: Exchange token for session (this creates session in WebView)
                            val exchangeUrl = "${BuildConfig.BASE_URL}/api/exchange-token.php?token=$token"

                            webView.loadUrl(exchangeUrl, getCustomHeaders())

                            // After token exchange, redirect to dashboard
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                val dashboardUrl = "${BuildConfig.BASE_URL}/dashboard.php"
                                webView.loadUrl(dashboardUrl, getCustomHeaders())
                            }, 1000) // Wait 1 second for token exchange
                        }, 300)
                    } else {
                        // No token (old flow) - just load dashboard
                        android.util.Log.w("YessFish", "No OAuth token received")
                        Toast.makeText(this, "⚠️ Login zonder token", Toast.LENGTH_SHORT).show()

                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            val dashboardUrl = "${BuildConfig.BASE_URL}/dashboard.php"
                            webView.loadUrl(dashboardUrl, getCustomHeaders())
                        }, 300)
                    }
                }
            }
        }
    }

    /**
     * Enable persistent cookies to keep users logged in
     * Cookies persist across app restarts AND UPDATES
     */
    private fun enablePersistentCookies() {
        val cookieManager = android.webkit.CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // CRITICAL: DO NOT remove session cookies - keep everything
        // This ensures cookies survive app updates

        // Flush cookies to storage immediately
        cookieManager.flush()

        android.util.Log.d("YessFish", "✅ Persistent cookies enabled - cookies will survive updates")
    }

    /**
     * Enable fullscreen immersive mode
     * Hides system UI (status bar, navigation bar) - WebView has NO URL bar by default
     */
    private fun enableFullscreenMode() {

        // CRITICAL: Remove action bar / title bar
        supportActionBar?.hide()

        // Enable fullscreen and hide decorations
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // For edge-to-edge experience
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) and above
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                // Hide both status bar and navigation bar
                controller.hide(
                    WindowInsets.Type.statusBars() or
                    WindowInsets.Type.navigationBars() or
                    WindowInsets.Type.systemBars()
                )
                // Stay in immersive mode even after user swipes
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
            // JavaScript (CRITICAL for Google Maps)
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true

            // DOM Storage (CRITICAL for Google Maps)
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

            // Mixed content - ALLOW for Google Maps compatibility
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            // Geolocation (CRITICAL for Google Maps location features)
            setGeolocationEnabled(true)

            // File access
            allowFileAccess = true
            allowContentAccess = true

            // Allow file access from file URLs (for Google Maps markers/icons)
            @Suppress("DEPRECATION")
            allowFileAccessFromFileURLs = true
            @Suppress("DEPRECATION")
            allowUniversalAccessFromFileURLs = true
        }

        // CRITICAL: Set custom User-Agent for app detection
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        // Use YessFish-Android User-Agent so server can detect the app
        val customUA = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Mobile Safari/537.36 YessFish-Android/$versionName"
        webView.settings.userAgentString = customUA

        android.util.Log.d("YessFish", "User-Agent set to: $customUA")

        // Enable safe browsing (disabled for debug builds to prevent login issues)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
            // Only enable for release builds
            val isDebug = BuildConfig.DEBUG
            WebSettingsCompat.setSafeBrowsingEnabled(webView.settings, !isDebug)
        }

        // WebViewClient with custom headers
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return true

                // Fishmap URLs should open native maps activity
                if (url.contains("fishmap.php") || url.contains("/fishmap")) {
                    val intent = Intent(this@MainActivity, FishmapActivity::class.java)
                    startActivity(intent)
                    return true
                }

                // Google OAuth URLs must open in Chrome Custom Tab
                // (Google blocks OAuth in embedded WebViews for security)
                if (url.contains("accounts.google.com") ||
                    url.contains("google.com/o/oauth2") ||
                    url.contains("googleapis.com/oauth")) {
                    // Open Google login in Chrome Custom Tab
                    openInCustomTab(url)
                    return true
                }

                // YessFish URLs: Let WebView handle it normally (don't reload)
                // App detection works via User-Agent and cookie injection
                if (url.contains("yessfish.com")) {
                    return false  // FALSE = let WebView handle navigation normally
                }

                // Block all other external URLs
                return true
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                // Custom headers are now injected via loadUrl() with headers
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // CRITICAL 1: Inject app detection cookie via JavaScript
                // This ensures the server ALWAYS knows we're in the Android app
                if (url?.contains("yessfish.com") == true) {
                    view?.evaluateJavascript("""
                        document.cookie = 'yessfish_android_app=true; path=/; max-age=31536000; secure; samesite=none';
                        console.log('[YessFish App] Cookie injected: yessfish_android_app=true');
                    """.trimIndent(), null)
                }

                // CRITICAL 2: Force flush cookies after EVERY page load
                // This ensures session cookies are saved immediately
                android.webkit.CookieManager.getInstance().flush()

                android.util.Log.d("YessFish", "Page finished: $url - cookie injected & flushed")
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
                    val tag = when (it.messageLevel()) {
                        ConsoleMessage.MessageLevel.ERROR -> "WebView-ERROR"
                        ConsoleMessage.MessageLevel.WARNING -> "WebView-WARN"
                        else -> "WebView-INFO"
                    }
                }
                return true
            }

            // JavaScript Alert/Confirm dialogs
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: android.webkit.JsResult?): Boolean {
                result?.confirm()
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
        // Check if user has session cookie - if yes, load dashboard directly
        val cookieManager = android.webkit.CookieManager.getInstance()
        val cookies = cookieManager.getCookie("yessfish.com") ?: ""

        // Check for session cookies (YESSFISH_SID or remember_me)
        val hasSessionCookie = cookies.contains("YESSFISH_SID=") &&
                              !cookies.contains("YESSFISH_SID=deleted") &&
                              cookies.split("YESSFISH_SID=").getOrNull(1)?.takeWhile { it != ';' }?.isNotEmpty() == true

        // If user has valid session, go directly to dashboard
        // Otherwise go to login page
        val url = if (hasSessionCookie) {
            android.util.Log.d("YessFish", "✅ Valid session found - loading dashboard")
            "${BuildConfig.BASE_URL}/dashboard.php"
        } else {
            android.util.Log.d("YessFish", "❌ No session - loading login page")
            "${BuildConfig.BASE_URL}/login.php"
        }

        android.util.Log.d("YessFish", "Cookies: $cookies")
        webView.loadUrl(url, getCustomHeaders())
    }

    /**
     * Open URL in Chrome Custom Tab (for Google OAuth)
     * Dit lost de "403 disallowed_useragent" error op
     */
    private fun openInCustomTab(url: String) {
        try {
            isOAuthInProgress = true  // Set flag - we're doing OAuth login

            // CRITICAL: Set cookie BEFORE opening Custom Tab
            // This allows the OAuth callback to detect Android app
            android.webkit.CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
                setCookie("yessfish.com", "yessfish_android_app=true; path=/; max-age=31536000")
                flush()
            }

            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback: open in external browser
            isOAuthInProgress = false
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

        // CRITICAL: Force flush cookies when app goes to background
        // This ensures session persists if Android kills the app
        android.webkit.CookieManager.getInstance().flush()
        android.util.Log.d("YessFish", "onPause - cookies flushed to disk")
    }

    override fun onStop() {
        super.onStop()

        // Double-flush cookies when app fully stops
        android.webkit.CookieManager.getInstance().flush()
        android.util.Log.d("YessFish", "onStop - cookies flushed to disk")
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        // Re-enable fullscreen mode when app resumes
        enableFullscreenMode()

        // CRITICAL: Only reload if WebView is truly empty (not just empty URL)
        // WebView can have content but empty URL after process restart
        val currentUrl = webView.url ?: ""
        val hasContent = webView.progress > 0 || currentUrl.contains("yessfish.com")

        android.util.Log.d("YessFish", "onResume - URL: $currentUrl, progress: ${webView.progress}, hasContent: $hasContent")

        // Only reload if WebView is completely empty (fresh start or killed by system)
        if (currentUrl.isEmpty() || currentUrl == "about:blank") {
            android.util.Log.d("YessFish", "WebView empty - reloading app")
            loadYessFish()
        } else {
            android.util.Log.d("YessFish", "WebView has content - not reloading")
        }
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
