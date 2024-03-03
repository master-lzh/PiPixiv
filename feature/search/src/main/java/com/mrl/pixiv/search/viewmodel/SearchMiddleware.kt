package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.repository.local.SearchLocalRepository
import com.mrl.pixiv.repository.remote.SearchRemoteRepository
import com.mrl.pixiv.util.queryParams

class SearchMiddleware(
    private val searchRemoteRepository: SearchRemoteRepository,
    private val searchLocalRepository: SearchLocalRepository,
) : Middleware<SearchState, SearchAction>() {
    override suspend fun process(state: SearchState, action: SearchAction) {
        when (action) {
            is SearchAction.SearchIllust -> searchIllust(state, action)
            is SearchAction.SearchIllustNext -> searchIllustNext(action)
            is SearchAction.SearchAutoComplete -> searchAutoComplete(action)
            is SearchAction.AddSearchHistory -> addSearchHistory(action)
            is SearchAction.DeleteSearchHistory -> deleteSearchHistory(action)
            is SearchAction.LoadSearchHistory -> loadSearchHistory()

            else -> {}
        }
    }

    private fun loadSearchHistory() {
        launchIO {
            searchLocalRepository.searchLocalSource.collect {
                dispatch(SearchAction.UpdateSearchHistory(it.searchHistoryList))
            }
        }
    }

    private fun addSearchHistory(action: SearchAction.AddSearchHistory) {
        launchIO { searchLocalRepository.addSearchHistory(action.searchWords) }
    }

    private fun deleteSearchHistory(action: SearchAction.DeleteSearchHistory) {
        launchIO { searchLocalRepository.deleteSearchHistory(action.searchWords) }
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
                        sort = state.searchFilter.sort,
                        searchTarget = state.searchFilter.searchTarget,
                        searchAiType = state.searchFilter.searchAiType,
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