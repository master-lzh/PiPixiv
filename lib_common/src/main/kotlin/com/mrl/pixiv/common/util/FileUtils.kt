package com.mrl.pixiv.common.util

import android.os.Environment
import android.text.TextUtils
import android.util.Pair
import androidx.annotation.CheckResult
import androidx.annotation.WorkerThread
import okio.BufferedSink
import okio.BufferedSource
import okio.Source
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun isExternalStorageAvailable(): Boolean {
    return isExternalStorageReadable() && isExternalStorageWritable()
}

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state
}

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable(): Boolean {
    val state = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
}

/***
 * 主要用来拷贝和保存文件,可以用于拷贝Asset,contentProvider和http返回的inputStream到指定文件
 */
@WorkerThread
fun copy(inputStream: InputStream, targetFile: File): Boolean {
    return copy(inputStream.source(), targetFile)
}

@WorkerThread
fun copy(originFile: File, targetFile: File): Boolean {
    return try {
        copy(originFile.source(), targetFile)
    } catch (e: Exception) {
        false
    }
}

@WorkerThread
fun copy(source: Source, targetFile: File): Boolean {
    var sink: BufferedSink? = null
    return try {
        sink = targetFile.sink().buffer()
        sink.writeAll(source)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        com.mrl.pixiv.common.util.closeQuietly(sink)
        com.mrl.pixiv.common.util.closeQuietly(source)
    }
}

/***
 * 读取文件的所有的内容到字符串,使用UTF-8编码
 */
@WorkerThread
@CheckResult
fun readFileToString(targetFile: File): String {
    var source: BufferedSource? = null
    try {
        source = targetFile.source().buffer()
        return source.readUtf8()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        com.mrl.pixiv.common.util.closeQuietly(source)
    }
    return ""
}


/***
 * 读取文件的所有的内容到字符串,使用UTF-8编码
 */
@WorkerThread
@CheckResult
fun readFileToString(sourceStream: InputStream): String {
    var source: BufferedSource? = null
    try {
        source = sourceStream.source().buffer()
        return source.readUtf8()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        com.mrl.pixiv.common.util.closeQuietly(source)
    }
    return ""
}

/***
 * 将字符串写入文件
 */
@WorkerThread
fun writeStringToFile(targetFile: File, string: String): Boolean {
    if (TextUtils.isEmpty(string)) {
        return false
    }
    var sink: BufferedSink? = null
    return try {
        sink = targetFile.sink().buffer()
        sink.write(string.toByteArray())
        true
    } catch (e: Exception) {
        false
    } finally {
        com.mrl.pixiv.common.util.closeQuietly(sink)
    }
}

/**
 * 写输出流到文件
 */
@WorkerThread
fun writeStreamToFile(inputStream: InputStream, outputFile: File): Boolean {
    var source: BufferedSource? = null
    var sink: BufferedSink? = null
    return try {
        source = inputStream.source().buffer()
        sink = outputFile.sink().buffer()
        sink.writeAll(source)
        true
    } catch (e: Exception) {
        false
    } finally {
        com.mrl.pixiv.common.util.closeQuietly(source)
        com.mrl.pixiv.common.util.closeQuietly(sink)
    }
}

/**
 * 写输出流到文件
 */
@WorkerThread
fun writeStreamToFile(inputStream: InputStream, pathName: String): Boolean {
    val outputFile = File(pathName)
    if (!createOrExistsFile(outputFile)) {
        return false
    }

    return writeStreamToFile(inputStream, outputFile)
}

/***
 * 将Object写入文件
 */
@WorkerThread
fun writeObjectToFile(pathName: String, o: Any): Boolean {
    var oos: ObjectOutputStream? = null
    var fos: FileOutputStream? = null
    val file = File(pathName)
    if (!createOrExistsFile(file)) {
        return false
    }

    return try {
        fos = FileOutputStream(file)
        oos = ObjectOutputStream(fos)
        oos.writeObject(o)
        oos.flush()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        closeQuietly(fos)
        closeQuietly(oos)
    }
}

/***
 * 读取文件内容为Object
 */
@WorkerThread
fun readFileToObject(pathName: String): Any? {
    var fis: FileInputStream? = null
    var ois: ObjectInputStream? = null
    var obj: Any? = null
    val file = File(pathName)
    return if (!file.exists()) {
        null
    } else {
        try {
            fis = FileInputStream(file)
            ois = ObjectInputStream(fis)
            obj = ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(ois)
            closeQuietly(fis)
        }
        obj
    }
}

fun deleteFiles(path: String): Boolean {
    val file = File(path)
    if (isFileExists(file)) {
        return deleteFiles(file)
    }

    return true
}


/**
 * 删除文件或者文件夹
 *
 * @param file 待删除的文件或文件夹
 * @return 删除是否成功
 */
@WorkerThread
fun deleteFiles(file: File): Boolean {
    if (file.isDirectory) {
        val children = file.list()
        if (children != null) {
            for (i in children.indices) {
                val success = deleteFiles(File(file, children[i]))
                if (!success) {
                    return false
                }
            }
        }
    }
    return file.delete()
}

/**
 * 删除文件或文件夹，返回删除的大小
 *
 * @param file 文件或文件夹
 * @return Pair.first 删除是否成功 <br></br>Pair.second 已删除文件大小
 */
fun deleteFilesAndGetSize(file: File): Pair<Boolean, Long> {
    var success = true
    var deletedSize: Long = 0
    if (file.isDirectory) {
        val children = file.list()
        if (children != null) {
            for (i in children.indices) {
                val tmp =
                    deleteFilesAndGetSize(File(file, children[i]))
                success = success && tmp.first
                deletedSize += tmp.second
            }
        }
        success = success && file.delete()
    } else {
        val sizeTmp = file.length()
        if (file.delete()) {
            deletedSize += sizeTmp
        } else {
            success = false
        }
    }
    return Pair(success, deletedSize)
}


/**
 * 移动文件
 */
fun renameTo(src: File, dst: File): Boolean {
    dst.delete()
    return try {
        dst.createNewFile()
        if (!src.renameTo(dst)) {
            copy(src, dst)
        } else true
    } catch (e: IOException) {
        dst.delete()
        false
    }
}

/**
 * 移动文件
 */
fun renameTo(src: String, dst: String): Boolean {
    return renameTo(File(src), File(dst))
}

/**
 * Return the file by path. If path is null or is blank, return null
 *
 * @param filePath The path of file.
 * @return the file
 */
fun getFileByPath(filePath: String?): File? {
    return if (filePath.isNullOrEmpty()) null else File(filePath)
}

/**
 * Return whether the file exists.
 *
 * @param filePath The path of file.
 * @return `true`: yes<br></br>`false`: no
 */
fun isFileExists(filePath: String?): Boolean {
    return isFileExists(getFileByPath(filePath))
}

/**
 * Return whether the file exists.
 *
 * @param file The file.
 * @return `true`: yes<br></br>`false`: no
 */
fun isFileExists(file: File?): Boolean {
    return file != null && file.exists()
}

/**
 * Create a directory if it doesn't exist, otherwise do nothing.
 *
 * @param dirPath The path of directory.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsDir(dirPath: String?): Boolean {
    return createOrExistsDir(getFileByPath(dirPath))
}

/**
 * Create a directory if it doesn't exist, otherwise do nothing.
 *
 * @param file The file.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsDir(file: File?): Boolean {
    return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
}

/**
 * Create a file if it doesn't exist, otherwise do nothing.
 *
 * @param filePath The path of file.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsFile(filePath: String?): Boolean {
    return createOrExistsFile(getFileByPath(filePath))
}

/**
 * Create a file if it doesn't exist, otherwise do nothing.
 *
 * @param file The file.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsFile(file: File?): Boolean {
    if (file == null) return false
    if (file.exists()) return file.isFile
    return if (!createOrExistsDir(file.parentFile)) false else try {
        file.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}


/**
 * Create a file if it doesn't exist, otherwise delete old file before creating.
 *
 * @param filePath The path of file.
 * @return `true`: success<br></br>`false`: fail
 */
fun createFileByDeleteOldFile(filePath: String?): Boolean {
    return createFileByDeleteOldFile(getFileByPath(filePath))
}

/**
 * Create a file if it doesn't exist, otherwise delete old file before creating.
 * return false if there is a same name dir exist.
 *
 * @param file The file.
 * @return `true`: success<br></br>`false`: fail
 */
fun createFileByDeleteOldFile(file: File?): Boolean {
    if (file == null) return false
    if (file.exists()) {
        if (!file.isFile) return false else if (!file.delete()) return false
    }
    // file exists and unsuccessfully delete then return false
    return if (!createOrExistsDir(file.parentFile)) false else try {
        file.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * Create a dir if it doesn't exist, otherwise delete old dir and its files before creating.
 *
 * @param dirPath The dir path
 * @return `true`: success<br></br>`false`: fail
 */
fun createDirByDeleteOldDir(dirPath: String?): Boolean {
    return createDirByDeleteOldDir(dirPath)
}

/**
 * Create a dir if it doesn't exist, otherwise delete old dir and its files before creating.
 *
 * @param dirFile The dir
 * @return `true`: success<br></br>`false`: fail
 */
fun createDirByDeleteOldDir(dirFile: File?): Boolean {
    if (dirFile == null) return false
    if (dirFile.exists()) {
        if (!dirFile.isDirectory) return false else if (!deleteFiles(dirFile)) return false
    }
    return if (!createOrExistsDir(dirFile.parentFile)) false else dirFile.mkdirs()
}


/**
 * Return the file's path of directory.
 *
 * @param file The file.
 * @return the file's path of directory
 */
fun getDirName(file: File?): String? {
    return if (file == null) "" else getDirName(file.absolutePath)
}

/**
 * Return the file's path of directory.
 *
 * @param filePath The path of file.
 * @return the file's path of directory
 */
fun getDirName(filePath: String): String? {
    if (TextUtils.isEmpty(filePath)) return ""
    val lastSep = filePath.lastIndexOf(File.separator)
    return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
}

/**
 * Return the name of file.
 *
 * @param file The file.
 * @return the name of file
 */
fun getFileName(file: File?): String? {
    return if (file == null) "" else getFileName(file.absolutePath)
}

/**
 * Return the name of file.
 *
 * @param filePath The path of file.
 * @return the name of file
 */
fun getFileName(filePath: String): String? {
    if (TextUtils.isEmpty(filePath)) return ""
    val lastSep = filePath.lastIndexOf(File.separator)
    return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
}

/**
 * Return the name of file without extension.
 *
 * @param file The file.
 * @return the name of file without extension
 */
fun getFileNameNoSuffix(file: File?): String? {
    return if (file == null) "" else getFileNameNoSuffix(file.path)
}

/**
 * Return the name of file without extension.
 *
 * @param filePath The path of file.
 * @return the name of file without extension
 */
fun getFileNameNoSuffix(filePath: String): String? {
    if (TextUtils.isEmpty(filePath)) return ""
    val lastPoi = filePath.lastIndexOf('.')
    val lastSep = filePath.lastIndexOf(File.separator)
    if (lastSep == -1) {
        return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
    }
    return if (lastPoi == -1 || lastSep > lastPoi) {
        filePath.substring(lastSep + 1)
    } else filePath.substring(lastSep + 1, lastPoi)
}

/**
 * Return the extension of file.
 *
 * @param file The file.
 * @return the extension of file
 */
fun getFileSuffix(file: File?): String? {
    return if (file == null) "" else getFileSuffix(file.path)
}

/**
 * Return the extension of file.
 *
 * @param filePath The path of file.
 * @return the extension of file
 */
fun getFileSuffix(filePath: String): String? {
    if (TextUtils.isEmpty(filePath)) return ""
    val lastPoi = filePath.lastIndexOf('.')
    val lastSep = filePath.lastIndexOf(File.separator)
    return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
}

/**
 * 将当前日期时间以特定格式生成文件名
 *
 * @param sdfFormat 格式String
 * @return 文件名
 */
fun generateFileNameByCurrentTime(sdfFormat: String): String {
    return generateFileNameByCurrentTime("", sdfFormat, "")
}

/**
 * 将当前日期时间以特定格式生成文件名
 *
 * @param sdf 时间格式
 * @return 文件名
 */
fun generateFileNameByCurrentTime(sdf: SimpleDateFormat): String {
    return generateFileNameByCurrentTime("", sdf, "")
}

/**
 * 将当前日期时间以特定格式生成文件名，并加上特定的前缀和后缀
 *
 * @param prefix    前缀
 * @param sdfFormat 格式String
 * @param suffix    后缀
 * @return 文件名
 */
fun generateFileNameByCurrentTime(
    prefix: String,
    sdfFormat: String,
    suffix: String,
): String {
    return generateFileNameByCurrentTime(prefix, SimpleDateFormat(sdfFormat, Locale.US), suffix)
}

/**
 * 将当前日期时间以特定格式生成文件名，并加上特定的前缀和后缀
 *
 * @param prefix 前缀
 * @param sdf    时间格式
 * @param suffix 后缀
 * @return 文件名
 */
fun generateFileNameByCurrentTime(
    prefix: String,
    sdf: SimpleDateFormat,
    suffix: String,
): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append(prefix)
        .append(sdf.format(Date()))
        .append(suffix)
    return stringBuilder.toString()
}

fun getFileSizeBytes(file: File): Long {
    return if (!file.exists()) 0 else file.length()
}

fun joinPaths(vararg paths: String): String {
    var file = File(paths[0])
    for (i in 1 until paths.size) {
        file = file.resolve(paths[i])
    }
    return file.path
}
