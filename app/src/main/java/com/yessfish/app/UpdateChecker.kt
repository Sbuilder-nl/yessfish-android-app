package com.yessfish.app

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * UpdateChecker - Controleert voor nieuwe app versies
 * Downloadt en installeert updates automatisch
 */
class UpdateChecker(private val context: Context) {

    companion object {
        private const val TAG = "YessFish-UpdateChecker"
        private const val VERSION_URL = "https://yessfish.com/api/version.json"
    }

    data class VersionInfo(
        val latestVersion: String,
        val latestVersionCode: Int,
        val minimumVersion: String,
        val minimumVersionCode: Int,
        val downloadUrl: String,
        val fileSize: String,
        val releaseDate: String,
        val forceUpdate: Boolean,
        val changelog: List<String>,
        val releaseNotes: String
    )

    /**
     * Check for updates (non-blocking)
     */
    suspend fun checkForUpdates(): VersionInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(VERSION_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                val currentLocale = getCurrentLocale()
                val changelogJson = json.getJSONObject("changelog")
                val changelogArray = if (changelogJson.has(currentLocale)) {
                    changelogJson.getJSONArray(currentLocale)
                } else {
                    changelogJson.getJSONArray("nl") // Fallback to Dutch
                }

                val changelog = mutableListOf<String>()
                for (i in 0 until changelogArray.length()) {
                    changelog.add(changelogArray.getString(i))
                }

                VersionInfo(
                    latestVersion = json.getString("latestVersion"),
                    latestVersionCode = json.getInt("latestVersionCode"),
                    minimumVersion = json.getString("minimumVersion"),
                    minimumVersionCode = json.getInt("minimumVersionCode"),
                    downloadUrl = json.getString("downloadUrl"),
                    fileSize = json.getString("fileSize"),
                    releaseDate = json.getString("releaseDate"),
                    forceUpdate = json.getBoolean("forceUpdate"),
                    changelog = changelog,
                    releaseNotes = json.getString("releaseNotes")
                )
            } else {
                Log.e(TAG, "Failed to fetch version info: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates: ${e.message}", e)
            null
        }
    }

    /**
     * Check if update is available
     */
    fun isUpdateAvailable(versionInfo: VersionInfo): Boolean {
        val currentVersionCode = getCurrentVersionCode()
        return versionInfo.latestVersionCode > currentVersionCode
    }

    /**
     * Check if update is required (force update)
     */
    fun isUpdateRequired(versionInfo: VersionInfo): Boolean {
        val currentVersionCode = getCurrentVersionCode()
        return versionInfo.forceUpdate && currentVersionCode < versionInfo.minimumVersionCode
    }

    /**
     * Show update dialog
     */
    fun showUpdateDialog(versionInfo: VersionInfo, onUpdateClick: () -> Unit) {
        val isRequired = isUpdateRequired(versionInfo)

        val message = buildString {
            append("📱 Nieuwe versie beschikbaar: ${versionInfo.latestVersion}\n\n")
            append("📦 Grootte: ${versionInfo.fileSize}\n")
            append("📅 Release datum: ${versionInfo.releaseDate}\n\n")
            append("🎉 Wat is er nieuw:\n")
            versionInfo.changelog.forEach { item ->
                append("$item\n")
            }
            if (isRequired) {
                append("\n⚠️ Deze update is verplicht om de app te blijven gebruiken.")
            }
        }

        AlertDialog.Builder(context)
            .setTitle(if (isRequired) "⚠️ Update Vereist" else "🎉 Update Beschikbaar")
            .setMessage(message)
            .setPositiveButton(if (isRequired) "Nu Updaten" else "Updaten") { _, _ ->
                onUpdateClick()
            }
            .apply {
                if (!isRequired) {
                    setNegativeButton("Later") { dialog, _ ->
                        dialog.dismiss()
                    }
                } else {
                    setCancelable(false)
                }
            }
            .show()
    }

    /**
     * Download and install update
     */
    fun downloadAndInstallUpdate(versionInfo: VersionInfo) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(Uri.parse(versionInfo.downloadUrl))
                .setTitle("YessFish Update")
                .setDescription("Downloaden van versie ${versionInfo.latestVersion}...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "yessfish-update-${versionInfo.latestVersion}.apk"
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)

            val downloadId = downloadManager.enqueue(request)

            // Listen for download completion
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        installUpdate(downloadManager, downloadId)
                        context.unregisterReceiver(this)
                    }
                }
            }

            context.registerReceiver(
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )

            Log.i(TAG, "Update download started: $downloadId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download update: ${e.message}", e)
            showErrorDialog("Download mislukt. Probeer het later opnieuw.")
        }
    }

    /**
     * Install the downloaded APK
     */
    private fun installUpdate(downloadManager: DownloadManager, downloadId: Long) {
        try {
            val uri = downloadManager.getUriForDownloadedFile(downloadId)

            if (uri != null) {
                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(installIntent)
                Log.i(TAG, "Install intent started")
            } else {
                Log.e(TAG, "Download URI is null")
                showErrorDialog("Installatie mislukt. Download opnieuw.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to install update: ${e.message}", e)
            showErrorDialog("Installatie mislukt: ${e.message}")
        }
    }

    /**
     * Show error dialog
     */
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle("❌ Fout")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Get current app version code
     */
    private fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get version code: ${e.message}", e)
            1
        }
    }

    /**
     * Get current locale (nl/en)
     */
    private fun getCurrentLocale(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return when (locale.language) {
            "nl" -> "nl"
            "en" -> "en"
            else -> "nl"
        }
    }
}
