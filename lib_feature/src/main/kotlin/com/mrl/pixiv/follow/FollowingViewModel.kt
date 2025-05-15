package com.mrl.pixiv.follow

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.datasource.local.mmkv.isSelf
import com.mrl.pixiv.common.repository.paging.FollowingPagingSource
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.state


class FollowingViewModel(
    private val uid: Long,
) : BaseMviViewModel<FollowingState, FollowingAction>(
    initialState = FollowingState(isSelf = uid.isSelf)
) {
    val followingPageSource = Pager(PagingConfig(pageSize = 30)) {
        FollowingPagingSource(uid, state.restrict)
    }.flow.cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: FollowingAction) {

    }
}