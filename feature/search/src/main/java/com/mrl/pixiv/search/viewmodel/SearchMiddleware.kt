package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.data.search.SearchIllustQuery
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.util.queryParams

class SearchMiddleware(
    private val searchRepository: SearchRepository,
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
            searchRepository.searchLocalSource.collect {
                dispatch(SearchAction.UpdateSearchHistory(it.searchHistoryList))
            }
        }
    }

    private fun addSearchHistory(action: SearchAction.AddSearchHistory) {
        launchIO { searchRepository.addSearchHistory(action.searchWords) }
    }

    private fun deleteSearchHistory(action: SearchAction.DeleteSearchHistory) {
        launchIO { searchRepository.deleteSearchHistory(action.searchWords) }
    }

    private fun searchAutoComplete(action: SearchAction.SearchAutoComplete) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRepository.searchAutoComplete(
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
                request = searchRepository.searchIllustNext(action.nextUrl.queryParams)
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

    private fun searchIllust(state: SearchState, action: SearchAction.SearchIllust) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = searchRepository.searchIllust(
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