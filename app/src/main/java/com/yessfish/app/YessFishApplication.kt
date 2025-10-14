package com.yessfish.app

import android.app.Application
import android.webkit.WebView

class YessFishApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enable WebView debugging in debug builds
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // Initialize any SDKs or services here
        // For example: Firebase, Crashlytics, Analytics, etc.
    }
}
