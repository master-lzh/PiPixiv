package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.repository.remote.SearchRemoteRepository
import com.mrl.pixiv.util.queryParams

class SearchMiddleware(
    private val searchRemoteRepository: SearchRemoteRepository,
) : Middleware<SearchState, SearchAction>() {
    override suspend fun process(state: SearchState, action: SearchAction) {
        when (action) {
            is SearchAction.SearchIllust -> searchIllust(state, action)
            is SearchAction.SearchIllustNext -> searchIllustNext(action)
            is SearchAction.SearchAutoComplete -> searchAutoComplete(action)

            else -> {}
        }
    }

    private fun searchAutoComplete(action: SearchAction.SearchAutoComplete) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRemoteRepository.searchAutoComplete(
                    SearchAutoCompleteQuery(
                        word = action.searchWords,
                    )
                )
            ) {
                dispatch(
                    SearchAction.UpdateAutoCompleteSearchWords(
                        autoCompleteSearchWords = it.tags
                    )
                )
            }
        }
    }

    private fun searchIllustNext(action: SearchAction.SearchIllustNext) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRemoteRepository.searchIllustNext(action.nextUrl.queryParams)
            ) {
                dispatch(
                    SearchAction.UpdateSearchIllustsResult(
                        illusts = it.illusts,
                        nextUrl = it.nextUrl,
                    )
                )
                action.callback()
            }
        }
    }

    private fun searchIllust(state: SearchState, action: SearchAction.SearchIllust) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRemoteRepository.searchIllust(
                    SearchIllustQuery(
                        word = action.searchWords,
                        sort = action.searchFilter.sort,
                        searchTarget = action.searchFilter.searchTarget,
                        searchAiType = action.searchFilter.searchAiType,
                    )
                ),
            ) {
                dispatch(
                    SearchAction.UpdateSearchIllustsResult(
                        illusts = it.illusts,
                        nextUrl = it.nextUrl,
                    )
                )
            }
        }
    }
}