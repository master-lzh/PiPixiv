package com.mrl.pixiv.picture.viewmodel

import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustDetailQuery
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.network.HttpManager
import com.mrl.pixiv.repository.IllustRepository
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.repository.UserRepository
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.PictureType
import com.mrl.pixiv.util.TAG
import com.mrl.pixiv.util.saveToAlbum
import com.mrl.pixiv.util.toBitmap
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Request
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds


class PictureMiddleware(
    private val illustRepository: IllustRepository,
    private val userRepository: UserRepository,
    private val searchRepository: SearchRepository,
) : Middleware<PictureState, PictureAction>() {
    private val httpManager: HttpManager by inject()
    override suspend fun process(state: PictureState, action: PictureAction) {
        when (action) {
            is PictureAction.GetIllustDetail -> getIllustDetail(action.illustId)
            is PictureAction.AddSearchHistory -> addSearchHistory(action.keyword)
            is PictureAction.GetUserIllustsIntent -> getUserIllusts(state, action.userId)
            is PictureAction.GetIllustRelatedIntent -> getIllustRelated(state, action.illustId)
            is PictureAction.LoadMoreIllustRelatedIntent -> loadMoreIllustRelated(
                state,
                action.queryMap
            )

            is PictureAction.BookmarkIllust -> bookmark(state, action.illustId)

            is PictureAction.UnBookmarkIllust -> unBookmark(state, action.illustId)
            is PictureAction.DownloadIllust -> downloadIllust(
                action.illustId,
                action.index,
                action.originalUrl,
                action.downloadCallback
            )

            is PictureAction.DownloadUgoira -> downloadUgoira(action.illustId)

            else -> {}
        }
    }

    private fun downloadUgoira(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.getUgoiraMetadata(illustId)
            ) {
                launchNetwork {
                    try {
                        val file = AppUtil.appContext.cacheDir.resolve("$illustId.zip")
                        if (file.exists() && file.length() > 0) {
                            val imageFiles =
                                unzipUgoira(ZipFile(file), illustId).mapIndexed { index, img ->
                                    img.toBitmap()!! to it.ugoiraMetadata.frames[index].delay
                                }
                            Log.e(TAG, "downloadUgoira: $imageFiles")
                            dispatch(PictureAction.UpdateUgoiraFrame(imageFiles))
                        } else {
                            val zipUrl = it.ugoiraMetadata.zipUrls.medium
                            val client = httpManager.imageOkHttpClient
                            val request = Request.Builder().url(zipUrl).build()
                            val response = client.newCall(request).execute()
                            if (response.isSuccessful) {
                                response.body.byteStream().use { inputStream ->
                                    file.outputStream().use { outputStream ->
                                        inputStream.copyTo(outputStream)
                                    }
                                }
                                // 解压
                                val imageFiles =
                                    unzipUgoira(ZipFile(file), illustId).mapIndexed { index, img ->
                                        img.toBitmap()!! to it.ugoiraMetadata.frames[index].delay
                                    }
                                Log.e(TAG, "downloadUgoira: $imageFiles")
                                dispatch(PictureAction.UpdateUgoiraFrame(imageFiles))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun unzipUgoira(zipFile: ZipFile, illustId: Long): MutableList<File> {
        val unzipDir = AppUtil.appContext.cacheDir.resolve("$illustId")
        val list = mutableListOf<File>()
        unzipDir.mkdirs()
        zipFile.entries().asSequence().forEach { zipEntry ->
            val newFile = File(unzipDir, zipEntry.name)
            if (zipEntry.isDirectory) {
                newFile.mkdirs()
            } else {
                if (!newFile.exists()) {
                    FileOutputStream(newFile).use { fileOutputStream ->
                        zipFile.getInputStream(zipEntry).use { inputStream ->
                            inputStream.copyTo(fileOutputStream)
                        }
                    }
                }
                list.add(newFile)
            }
        }
        return list
    }

    private fun getIllustDetail(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.getIllustDetail(
                    IllustDetailQuery(
                        illustId = illustId,
                        filter = Filter.ANDROID.value
                    )
                )
            ) {
                dispatch(PictureAction.UpdateIllust(it.illust))
                dispatch(PictureAction.GetUserIllustsIntent(it.illust.user.id))
                dispatch(PictureAction.GetIllustRelatedIntent(it.illust.id))
            }
        }
    }

    private fun addSearchHistory(keyword: String) {
        searchRepository.addSearchHistory(keyword)
    }

    private fun downloadIllust(
        illustId: Long,
        index: Int,
        originalUrl: String,
        downloadCallback: (result: Boolean) -> Unit
    ) {
        launchNetwork {
            // 使用coil下载图片
            val imageLoader = Coil.imageLoader(AppUtil.appContext)
            val request = ImageRequest.Builder(AppUtil.appContext)
                .data(originalUrl)
                .build()
            val result = withTimeoutOrNull(10.seconds) {
                imageLoader.execute(request)
            }
            result ?: run {
                downloadCallback(false)
                return@launchNetwork
            }
            val file =
                result.drawable?.toBitmap()?.saveToAlbum("${illustId}_$index", PictureType.PNG)
            if (file != null) {
                downloadCallback(true)
            }
        }
    }

    private fun unBookmark(state: PictureState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(
                        illustId
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateIsBookmarkState(
                        userIllusts = state.userIllusts.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = false))
                            }
                        },
                        illustRelated = state.illustRelated.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = false))
                            }
                        }
                    )
                )
            }
        }
    }

    private fun bookmark(state: PictureState, illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkAdd(IllustBookmarkAddReq(illustId))
            ) {
                dispatch(
                    PictureAction.UpdateIsBookmarkState(
                        userIllusts = state.userIllusts.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = true))
                            }
                        },
                        illustRelated = state.illustRelated.apply {
                            indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                                set(it, get(it).copy(isBookmarked = true))
                            }
                        }
                    )
                )
            }
        }
    }

    private fun loadMoreIllustRelated(state: PictureState, queryMap: Map<String, String>?) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.loadMoreIllustRelated(
                    queryMap ?: return@launchNetwork
                )
            ) {
                dispatch(
                    PictureAction.UpdateIllustRelatedState(
                        illustRelated = state.illustRelated + it.illusts,
                        nextUrl = it.nextURL
                    )
                )
            }
        }

    private fun getIllustRelated(state: PictureState, illustId: Long) =
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.getIllustRelated(
                    IllustRelatedQuery(
                        illustId = illustId,
                        filter = Filter.ANDROID.value
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateIllustRelatedState(
                        illustRelated = it.illusts,
                        nextUrl = it.nextURL
                    )
                )
            }
        }

    private fun getUserIllusts(state: PictureState, userId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = userRepository.getUserIllusts(
                    UserIllustsQuery(
                        userId = userId,
                        type = Type.Illust.value
                    )
                )
            ) {
                dispatch(
                    PictureAction.UpdateUserIllustsState(userIllusts = it.illusts)
                )
            }
        }
    }
}