package com.vaccine.slot.notifier.ui.base

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.vaccine.slot.notifier.helper.NetworkHelper
import com.vaccine.slot.notifier.other.Constants.UPDATED_APK_FILE_NAME
import java.io.File
import javax.inject.Inject


open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var networkHelper: NetworkHelper
    fun isInternetAvailable(): Boolean {
        return networkHelper.isNetworkConnected()
    }

    fun downloadUpdate(url: String) {

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var destination: String = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        val fileName = UPDATED_APK_FILE_NAME
        destination += fileName
        val uri: Uri = Uri.parse("file://$destination")
        val file = File(destination)
        if (file.exists()) file.delete()
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationUri(uri)
        request.setNotificationVisibility(VISIBILITY_VISIBLE)
        dm.enqueue(request)

        val finalDestination = destination
        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    val contentUri: Uri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", File(finalDestination))
//                    val openFileIntent = Intent(Intent.ACTION_VIEW)
//                    openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    openFileIntent.data = contentUri
//                    startActivity(openFileIntent)
//                    unregisterReceiver(this)
//                    finish()
//                } else {
//                    val install = Intent(Intent.ACTION_VIEW)
//                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    install.setDataAndType(uri,
//                            "application/vnd.android.package-archive")
//                    startActivity(install)
//                    unregisterReceiver(this)
//                    finish()
//                }
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}