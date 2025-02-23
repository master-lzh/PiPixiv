package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.data.search.SearchAutoCompleteQuery
import com.mrl.pixiv.common.repository.SearchRepository
import com.mrl.pixiv.common.viewmodel.Middleware
import org.koin.core.annotation.Factory

@Factory
class SearchMiddleware(
    private val searchRepository: SearchRepository,
) : Middleware<SearchState, SearchAction>() {
    override suspend fun process(state: SearchState, action: SearchAction) {
        when (action) {
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

}