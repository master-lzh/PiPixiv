package com.mrl.pixiv.picture.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Type
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

@Stable
data class PictureState(
    val illust: Illust?,
    val illustRelated: SnapshotStateList<Illust>,
    val userIllusts: SnapshotStateList<Illust>,
    val nextUrl: String,
    val ugoiraImages: ImmutableList<Pair<Bitmap, Long>>,
) : State {
    companion object {
        val INITIAL = PictureState(
            illust = null,
            illustRelated = mutableStateListOf(),
            userIllusts = mutableStateListOf(),
            nextUrl = "",
            ugoiraImages = persistentListOf()
        )
    }
}

sealed class PictureAction : Action {
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

    data class UpdateState(val state: PictureState) : PictureAction()

    data class UpdateUserIllustsState(val userIllusts: List<Illust>) :
        PictureAction()

    data class UpdateIllustRelatedState(val illustRelated: List<Illust>, val nextUrl: String) :
        PictureAction()

    data class UpdateIsBookmarkState(
        val userIllusts: List<Illust>,
        val illustRelated: List<Illust>
    ) : PictureAction()

    data class UpdateIllust(val illust: Illust) : PictureAction()

    data class UpdateUgoiraFrame(val images: List<Pair<Bitmap, Long>>) : PictureAction()

    data class DownloadIllust(
        val illustId: Long,
        val index: Int,
        val originalUrl: String,
        val downloadCallback: (result: Boolean) -> Unit
    ) : PictureAction()
}

@KoinViewModel
class PictureViewModel(
    illust: Illust,
    reducer: PictureReducer,
    middleware: PictureMiddleware,
) : BaseViewModel<PictureState, PictureAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = PictureState.INITIAL,
) {
    init {
        dispatch(PictureAction.GetUserIllustsIntent(illust.user.id))
        dispatch(PictureAction.GetIllustRelatedIntent(illust.id))
        if (illust.type == Type.Ugoira) {
            dispatch(PictureAction.DownloadUgoira(illust.id))
        }
    }

    override fun onStart() {

    }
}

@KoinViewModel
class PictureDeeplinkViewModel(
    illustId: Long,
    reducer: PictureReducer,
    middleware: PictureMiddleware,
) : BaseViewModel<PictureState, PictureAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = PictureState.INITIAL,
) {
    init {
        dispatch(PictureAction.GetIllustDetail(illustId))
    }
}