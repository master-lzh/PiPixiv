package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Type

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