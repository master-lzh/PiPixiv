package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.base.BaseViewModel

class PictureViewModel(
    reducer: PictureReducer,
    middleware: PictureMiddleware,
) : BaseViewModel<PictureState, PictureAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = PictureState.INITIAL,
) {
    override fun onStart() {

    }
}