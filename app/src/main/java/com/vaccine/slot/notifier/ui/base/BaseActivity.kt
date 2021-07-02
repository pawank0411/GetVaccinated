package com.vaccine.slot.notifier.ui.base

import android.app.DownloadManager
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
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            var destination: String =
                this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
            val fileName = UPDATED_APK_FILE_NAME
            destination += fileName
            val file = File(destination)
            if (file.exists()) file.delete()
            val uri: Uri = Uri.parse("file://$destination")
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("GetVaccinated")
            request.setDescription("Downloading updates...")
            request.setDestinationUri(uri)
            dm.enqueue(request)
        } catch (e: Exception) {
            /* no op */
        }
    }
}