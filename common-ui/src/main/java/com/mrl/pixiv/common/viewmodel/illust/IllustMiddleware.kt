package com.mrl.pixiv.common.viewmodel.illust

import com.mrl.pixiv.common.viewmodel.Middleware

class IllustMiddleware : Middleware<IllustState, IllustAction>() {
    override suspend fun process(state: IllustState, action: IllustAction) {
    }
}