package com.mrl.pixiv.util

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


// 下载文件夹为DCIM/PiPixiv
const val DOWNLOAD_DIR = "PiPixiv"
fun Bitmap.saveToAlbum(fileName: String): File? {
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        .absolutePath
        .let { path ->
            val dir = "$path/$DOWNLOAD_DIR"
            createOrExistsDir(dir)
            val file = "$dir/$fileName.png"
            FileOutputStream(file).use { out ->
                if (compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    return File(file)
                }
            }
        }
    return null
}

fun calculateImageSize(url: String): Float {
    return try {
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = "HEAD"
        connection.setRequestProperty("Referer", "https://www.pixiv.net/")
        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val contentLength = connection.getHeaderField("Content-Length")
            if (contentLength != null) {
                val sizeInBytes = contentLength.toLong()
                return sizeInBytes / 1024f / 1024f
            }
        }
        connection.disconnect()
        return 0f
    } catch (e: Exception) {
        e.printStackTrace()
        0f
    }
}