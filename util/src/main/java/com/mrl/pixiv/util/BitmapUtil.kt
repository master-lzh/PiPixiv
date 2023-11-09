package com.mrl.pixiv.util

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


// 下载文件夹为DCIM/PiPixiv
const val DOWNLOAD_DIR = "PiPixiv"

enum class PictureType(val extension: String) {
    PNG(".png"),
    JPG(".jpg"),
    JPEG(".jpeg");

    fun parseType(extension: String): PictureType {
        return when (extension) {
            ".png" -> PNG
            ".jpg" -> JPG
            ".jpeg" -> JPEG
            else -> PNG
        }
    }
}

fun Bitmap.saveToAlbum(fileName: String, type: PictureType): File? {
    val compressFormat = when (type) {
        PictureType.PNG -> Bitmap.CompressFormat.PNG
        PictureType.JPEG -> Bitmap.CompressFormat.JPEG
        PictureType.JPG -> Bitmap.CompressFormat.JPEG
    }
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        .absolutePath
        .let { path ->
            val dir = joinPaths(path, DOWNLOAD_DIR)
            createOrExistsDir(dir)
            val file = joinPaths(dir, "$fileName${type.extension}")
            FileOutputStream(file).use { out ->
                if (compress(compressFormat, 100, out)) {
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