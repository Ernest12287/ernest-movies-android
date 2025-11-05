// ============================================
// FILE: UpdateChecker.kt
// LOCATION: Ernest-Movies-app/app/src/main/java/com/ernestmovies/app/services/UpdateChecker.kt
// ============================================
package com.ernestmovies.app.services

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.ernestmovies.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class UpdateChecker(private val context: Context) {
    
    companion object {
        private const val GITHUB_API_URL = "https://api.github.com/repos/Ernest12287/ernest-movies-android/releases/latest"
        private const val PREFS_NAME = "ernest_movies_prefs"
        private const val LAST_UPDATE_CHECK = "last_update_check"
    }
    
    suspend fun checkForUpdates() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastCheck = prefs.getLong(LAST_UPDATE_CHECK, 0)
        val currentTime = System.currentTimeMillis()
        
        // Check once per day
        if (currentTime - lastCheck < 24 * 60 * 60 * 1000) {
            return
        }
        
        prefs.edit().putLong(LAST_UPDATE_CHECK, currentTime).apply()
        
        try {
            val latestRelease = fetchLatestRelease() ?: return
            val latestVersion = latestRelease.getString("tag_name").removePrefix("v")
            val currentVersion = BuildConfig.VERSION_NAME
            
            if (isNewerVersion(latestVersion, currentVersion)) {
                val downloadUrl = latestRelease.getJSONArray("assets")
                    .getJSONObject(0)
                    .getString("browser_download_url")
                
                showUpdateDialog(latestVersion, downloadUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun fetchLatestRelease(): JSONObject? = withContext(Dispatchers.IO) {
        try {
            val url = URL(GITHUB_API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                JSONObject(response)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        
        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val latestPart = latestParts.getOrNull(i) ?: 0
            val currentPart = currentParts.getOrNull(i) ?: 0
            
            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }
        
        return false
    }
    
    private fun showUpdateDialog(version: String, downloadUrl: String) {
        AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("Version $version is available. Would you like to download it?")
            .setPositiveButton("Download") { _, _ ->
                downloadUpdate(downloadUrl)
            }
            .setNegativeButton("Later", null)
            .show()
    }
    
    private fun downloadUpdate(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}