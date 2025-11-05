// ============================================
// FILE: ErnestMoviesApplication.kt
// LOCATION: Ernest-Movies-app/app/src/main/java/com/ernestmovies/app/ErnestMoviesApplication.kt
// ============================================
package com.ernestmovies.app

import android.app.Application
import android.webkit.WebView

class ErnestMoviesApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Enable WebView debugging in debug builds
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
    
    companion object {
        lateinit var instance: ErnestMoviesApplication
            private set
    }
}