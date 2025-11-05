package com.ernestmovies.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ernestmovies.app.databinding.ActivityMainBinding
import com.ernestmovies.app.services.UpdateChecker
import com.ernestmovies.app.utils.NetworkUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var updateChecker: UpdateChecker
    private var isWebViewLoaded = false
    
    companion object {
        private const val WEBSITE_URL = "https://ernest-movies.vercel.app"
        private const val TELEGRAM_URL = "https://t.me/ernesttechhouse"
        private const val WHATSAPP_URL = "https://whatsapp.com/channel/0029VayK4ty7DAWr0jeCZx0i"
        private const val EMAIL = "peaseernest8@gmail.com"
        private const val PHONE = "+254793585908"
    }
    
    // Network change listener
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                // Network is back, reload if there was an error
                if (binding.errorLayout.visibility == View.VISIBLE) {
                    loadWebsite()
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Enable ActionBar for menu
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Ernest Movies"
        
        updateChecker = UpdateChecker(this)
        
        setupWebView()
        checkForUpdates()
        loadWebsite()
        
        // Register network listener
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
        
        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
        }
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
                builtInZoomControls = false
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                mediaPlaybackRequiresUserGesture = false
                javaScriptCanOpenWindowsAutomatically = true
            }
            
            // Fix scroll issue - disable nested scrolling
            isNestedScrollingEnabled = true
            
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                }
                
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    isWebViewLoaded = true
                }
                
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    // Only show error for main frame failures
                    if (request?.isForMainFrame == true) {
                        showError()
                    }
                }
                
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                }
                
                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                }
            }
        }
    }
    
    private fun loadWebsite() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            binding.webView.loadUrl(WEBSITE_URL)
            binding.errorLayout.visibility = View.GONE
        } else {
            showError()
        }
    }
    
    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.swipeRefresh.isRefreshing = false
        binding.errorLayout.visibility = View.VISIBLE
        
        binding.retryButton.setOnClickListener {
            loadWebsite()
        }
    }
    
    private fun checkForUpdates() {
        lifecycleScope.launch {
            updateChecker.checkForUpdates()
        }
    }
    
    // Create menu for channel links
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_telegram -> {
                openUrl(TELEGRAM_URL)
                true
            }
            R.id.menu_whatsapp -> {
                openUrl(WHATSAPP_URL)
                true
            }
            R.id.menu_email -> {
                sendEmail()
                true
            }
            R.id.menu_call -> {
                makeCall()
                true
            }
            R.id.menu_refresh -> {
                binding.webView.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to opening in WebView
            binding.webView.loadUrl(url)
        }
    }
    
    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$EMAIL")
            putExtra(Intent.EXTRA_SUBJECT, "Ernest Movies App - Contact")
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun makeCall() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$PHONE"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        // Unregister network listener
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.webView.destroy()
        super.onDestroy()
    }
}