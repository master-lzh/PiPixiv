package com.mrl.pixiv.picture.viewmodel

import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import com.mrl.pixiv.common.data.HttpClientEnum
import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Type
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustDetailQuery
import com.mrl.pixiv.data.illust.IllustRelatedQuery
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.picture.R
import com.mrl.pixiv.repository.IllustRepository
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.repository.UserRepository
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.PictureType
import com.mrl.pixiv.util.TAG
import com.mrl.pixiv.util.saveToAlbum
import com.mrl.pixiv.util.toBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Factory
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds

@Factory
class PictureMiddleware(
    private val illustRepository: IllustRepository,
    private val userRepository: UserRepository,
    private val searchRepository: SearchRepository,
) : Middleware<PictureState, PictureAction>() {
    private val imageOkHttpClient: HttpClient by inject(named(HttpClientEnum.IMAGE))
    override suspend fun process(state: PictureState, action: PictureAction) {
        when (action) {
            is PictureAction.GetIllustDetail -> getIllustDetail(action.illustId)
            is PictureAction.AddSearchHistory -> addSearchHistory(action.keyword)
            is PictureAction.GetUserIllustsIntent -> getUserIllusts(action.userId)
            is PictureAction.GetIllustRelatedIntent -> getIllustRelated(action.illustId)
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
                            val response = imageOkHttpClient.request {
                                url(zipUrl)
                            }
                            if (response.status.isSuccess()) {
                                response.bodyAsChannel().toInputStream().use { inputStream ->
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
            val imageLoader = SingletonImageLoader.get(AppUtil.appContext)
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
            result.image?.asDrawable(AppUtil.appContext.resources)?.toBitmap()
                ?.saveToAlbum("${illustId}_$index", PictureType.PNG) {
                with(AppUtil.appContext) {
                    downloadCallback(it)
                    dispatchError(Exception(getString(if (it) R.string.download_success else R.string.download_failed)))
                }
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

    private fun getIllustRelated(illustId: Long) =
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

    private fun getUserIllusts(userId: Long) {
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