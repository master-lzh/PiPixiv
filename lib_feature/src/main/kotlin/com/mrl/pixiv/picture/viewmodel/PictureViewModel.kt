package com.mrl.pixiv.picture.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.core.graphics.drawable.toBitmap
import coil3.SingletonImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.HttpClientEnum
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Type
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.repository.SearchRepository
import com.mrl.pixiv.common.util.*
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import kotlin.time.Duration.Companion.seconds

@Stable
data class PictureState(
    val illust: Illust? = null,
    val illustRelated: SnapshotStateList<Illust> = mutableStateListOf(),
    val userIllusts: SnapshotStateList<Illust> = mutableStateListOf(),
    val nextUrl: String = "",
    val ugoiraImages: ImmutableList<Pair<Bitmap, Long>> = persistentListOf(),
)

sealed class PictureAction : ViewIntent {
    data class GetIllustDetail(val illustId: Long) : PictureAction()
    data class AddSearchHistory(val keyword: String) : PictureAction()
    data class GetUserIllustsIntent(
        val userId: Long,
    ) : PictureAction()

    data class GetIllustRelatedIntent(
        val illustId: Long,
    ) : PictureAction()

    data class LoadMoreIllustRelatedIntent(
        val queryMap: Map<String, String>? = null,
    ) : PictureAction()

    data class BookmarkIllust(val illustId: Long) : PictureAction()

    data class UnBookmarkIllust(val illustId: Long) : PictureAction()

    data class DownloadUgoira(val illustId: Long) : PictureAction()

    data class DownloadIllust(
        val illustId: Long,
        val index: Int,
        val originalUrl: String,
        val downloadCallback: (result: Boolean) -> Unit
    ) : PictureAction()
}

@KoinViewModel
class PictureViewModel(
    illust: Illust?,
    illustId: Long?,
) : BaseMviViewModel<PictureState, PictureAction>(
    initialState = PictureState(),
), KoinComponent {
    private val imageOkHttpClient: HttpClient by inject(named(HttpClientEnum.IMAGE))

    override suspend fun handleIntent(intent: PictureAction) {
        when (intent) {
            is PictureAction.GetIllustDetail -> getIllustDetail(intent.illustId)
            is PictureAction.AddSearchHistory -> addSearchHistory(intent.keyword)
            is PictureAction.GetUserIllustsIntent -> getUserIllusts(intent.userId)
            is PictureAction.GetIllustRelatedIntent -> getIllustRelated(intent.illustId)
            is PictureAction.LoadMoreIllustRelatedIntent -> loadMoreIllustRelated(
                intent.queryMap
            )

            is PictureAction.BookmarkIllust -> bookmark(intent.illustId)

            is PictureAction.UnBookmarkIllust -> unBookmark(intent.illustId)
            is PictureAction.DownloadIllust -> downloadIllust(
                intent.illustId,
                intent.index,
                intent.originalUrl,
                intent.downloadCallback
            )

            is PictureAction.DownloadUgoira -> downloadUgoira(intent.illustId)
        }
    }

    init {
        when {
            illust != null -> {
                dispatch(PictureAction.GetUserIllustsIntent(illust.user.id))
                dispatch(PictureAction.GetIllustRelatedIntent(illust.id))
                if (illust.type == Type.Ugoira) {
                    dispatch(PictureAction.DownloadUgoira(illust.id))
                }
            }

            illustId != null -> {
                dispatch(PictureAction.GetIllustDetail(illustId))
            }
        }
    }

    private fun downloadUgoira(illustId: Long) {
        launchIO {
            val resp = PixivRepository.getUgoiraMetadata(illustId)
            val file = AppUtil.appContext.cacheDir.resolve("$illustId.zip")
            if (file.exists() && file.length() > 0) {
                val imageFiles =
                    unzipUgoira(ZipFile(file), illustId).mapIndexed { index, img ->
                        img.toBitmap()!! to resp.ugoiraMetadata.frames[index].delay
                    }
                Log.e(TAG, "downloadUgoira: $imageFiles")
                updateState {
                    copy(ugoiraImages = imageFiles.toImmutableList())
                }
            } else {
                val zipUrl = resp.ugoiraMetadata.zipUrls.medium
                val response = imageOkHttpClient.request {
                    url.takeFrom(zipUrl)
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
                            img.toBitmap()!! to resp.ugoiraMetadata.frames[index].delay
                        }
                    Log.e(TAG, "downloadUgoira: $imageFiles")
                    updateState {
                        copy(ugoiraImages = imageFiles.toImmutableList())
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
        launchIO {
            val resp = PixivRepository.getIllustDetail(
                illustId = illustId,
                filter = Filter.ANDROID.value
            )
            updateState {
                copy(illust = resp.illust)
            }
            getUserIllusts(resp.illust.user.id)
            getIllustRelated(resp.illust.id)
        }
    }

    private fun addSearchHistory(keyword: String) {
        SearchRepository.addSearchHistory(keyword)
    }

    private fun downloadIllust(
        illustId: Long,
        index: Int,
        originalUrl: String,
        downloadCallback: (result: Boolean) -> Unit
    ) {
        launchIO {
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
                return@launchIO
            }
            result.image?.asDrawable(AppUtil.appContext.resources)?.toBitmap()
                ?.saveToAlbum("${illustId}_$index", PictureType.PNG) {
                    with(AppUtil.appContext) {
                        downloadCallback(it)
                        handleError(Exception(getString(if (it) RString.download_success else RString.download_failed)))
                    }
                }
        }
    }

    private fun unBookmark(illustId: Long) {
        launchIO {
            PixivRepository.postIllustBookmarkDelete(illustId)
            updateState {
                copy(
                    userIllusts = userIllusts.apply {
                        indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                            set(it, get(it).copy(isBookmarked = false))
                        }
                    },
                    illustRelated = illustRelated.apply {
                        indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                            set(it, get(it).copy(isBookmarked = false))
                        }
                    }
                )
            }
        }
    }

    private fun bookmark(illustId: Long) {
        launchIO {
            PixivRepository.postIllustBookmarkAdd(illustId)
            updateState {
                copy(
                    userIllusts = userIllusts.apply {
                        indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                            set(it, get(it).copy(isBookmarked = true))
                        }
                    },
                    illustRelated = illustRelated.apply {
                        indexOfFirst { it.id == illustId }.takeIf { it != -1 }?.let {
                            set(it, get(it).copy(isBookmarked = true))
                        }
                    }
                )
            }
        }
    }

    private fun loadMoreIllustRelated(queryMap: Map<String, String>?) =
        launchIO {
            val resp = PixivRepository.loadMoreIllustRelated(
                queryMap ?: return@launchIO
            )
            updateState {
                copy(
                    illustRelated = (illustRelated + resp.illusts).toMutableStateList(),
                    nextUrl = resp.nextURL
                )
            }
        }

    private fun getIllustRelated(illustId: Long) =
        launchIO {
            val resp = PixivRepository.getIllustRelated(
                illustId = illustId,
                filter = Filter.ANDROID.value
            )
            updateState {
                copy(
                    illustRelated = resp.illusts.toMutableStateList(),
                    nextUrl = resp.nextURL
                )
            }
        }

    private fun getUserIllusts(userId: Long) {
        launchIO {
            val resp = PixivRepository.getUserIllusts(
                userId = userId,
                type = Type.Illust.value
            )
            updateState {
                copy(userIllusts = resp.illusts.toMutableStateList())
            }
        }
    }
}