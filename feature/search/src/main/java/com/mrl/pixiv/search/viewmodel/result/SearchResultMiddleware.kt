package com.mrl.pixiv.search.viewmodel.result

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.util.queryParams
import org.koin.core.annotation.Factory

@Factory
class SearchResultMiddleware(
    private val searchRepository: SearchRepository,
) : Middleware<SearchResultState, SearchResultAction>() {
    override suspend fun process(state: SearchResultState, action: SearchResultAction) {
        when (action) {
            is SearchResultAction.SearchIllust -> searchIllust(action)
            is SearchResultAction.SearchIllustNext -> searchIllustNext(action)
            else -> Unit
        }
    }

    private fun searchIllust(action: SearchResultAction.SearchIllust) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRepository.searchIllust(
                    SearchIllustQuery(
                        word = action.searchWords,
                        sort = state().searchFilter.sort,
                        searchTarget = state().searchFilter.searchTarget,
                        searchAiType = state().searchFilter.searchAiType,
                    )
                ),
            ) {
                dispatch(
                    SearchResultAction.UpdateSearchIllustsResult(
                        illusts = it.illusts,
                        nextUrl = it.nextUrl,
                        initial = true,
                    )
                )
            }
        }
    }

    private fun searchIllustNext(action: SearchResultAction.SearchIllustNext) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRepository.searchIllustNext(action.nextUrl.queryParams)
            ) {
                dispatch(
                    SearchResultAction.UpdateSearchIllustsResult(
                        illusts = it.illusts,
                        nextUrl = it.nextUrl,
                        initial = false,
                    )
                )
            }
        }
    }
}