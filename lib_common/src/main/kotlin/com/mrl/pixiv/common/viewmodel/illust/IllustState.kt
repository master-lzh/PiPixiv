package com.mrl.pixiv.common.viewmodel.illust

import androidx.collection.LruCache
import com.mrl.pixiv.common.data.Illust

object IllustState {
    val illusts: LruCache<Long, Illust> = LruCache(100)

    fun setIllust(illustId: Long, illust: Illust) {
        illusts.put(illustId, illust)
    }
}