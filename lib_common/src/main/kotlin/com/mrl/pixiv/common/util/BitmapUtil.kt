package com.mrl.pixiv.common.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFile
import android.os.Environment
import okio.Path.Companion.toPath
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

fun Bitmap.saveToAlbum(
    fileName: String,
    type: PictureType,
    callback: (Boolean) -> Unit = {}
) {
    val compressFormat = when (type) {
        PictureType.PNG -> Bitmap.CompressFormat.PNG
        PictureType.JPEG -> Bitmap.CompressFormat.JPEG
        PictureType.JPG -> Bitmap.CompressFormat.JPEG
    }
    try {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .absolutePath
            .let { path ->
                val dir = path.toPath() / DOWNLOAD_DIR
                dir.toFile().mkdirs()
                val filePath = dir / "$fileName${type.extension}"
                FileOutputStream(filePath.toFile()).use { out ->
                    if (compress(compressFormat, 100, out)) {
                        callback(true)
                    }
                }
            }
    } catch (_: Exception) {
        callback(false)
    }
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

fun File.toBitmap(): Bitmap? {
    return try {
        decodeFile(absolutePath)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}