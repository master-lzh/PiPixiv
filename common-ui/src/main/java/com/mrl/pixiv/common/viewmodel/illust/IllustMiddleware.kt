package com.mrl.pixiv.common.viewmodel.illust

import com.mrl.pixiv.common.viewmodel.Middleware
import org.koin.core.annotation.Factory

@Factory
class IllustMiddleware : Middleware<IllustState, IllustAction>() {
    override suspend fun process(state: IllustState, action: IllustAction) {
    }
}