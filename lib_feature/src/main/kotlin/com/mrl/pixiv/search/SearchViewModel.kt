package com.mrl.pixiv.search

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.coroutine.withIOContext
import com.mrl.pixiv.common.data.Tag
import com.mrl.pixiv.common.data.search.*
import com.mrl.pixiv.common.mmkv.MMKVUser
import com.mrl.pixiv.common.mmkv.asStateFlow
import com.mrl.pixiv.common.mmkv.mmkvSerializable
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

@Stable
data class SearchState(
    val searchWords: String = "",
    val autoCompleteSearchWords: ImmutableList<Tag> = persistentListOf(),
) {
    data class SearchFilter(
        val sort: SearchSort = SearchSort.POPULAR_DESC,
        val searchTarget: SearchTarget = SearchTarget.PARTIAL_MATCH_FOR_TAGS,
        val searchAiType: SearchAiType = SearchAiType.HIDE_AI,
    )
}

sealed class SearchAction : ViewIntent {
    data object ClearAutoCompleteSearchWords : SearchAction()

    data class UpdateSearchWords(
        val searchWords: String,
    ) : SearchAction()

    data class SearchAutoComplete(
        val searchWords: String,
        val mergePlainKeywordResults: Boolean = true,
    ) : SearchAction()


    data class AddSearchHistory(
        val searchWords: String,
    ) : SearchAction()

    data class DeleteSearchHistory(
        val searchWords: String,
    ) : SearchAction()
}

@KoinViewModel
class SearchViewModel : BaseMviViewModel<SearchState, SearchAction>(
    initialState = SearchState()
), MMKVUser {
    private val searchHistory by mmkvSerializable(Search()).asStateFlow()
    val searchHistoryFlow = searchHistory.asStateFlow()

    override suspend fun handleIntent(intent: SearchAction) {
        when (intent) {
            is SearchAction.SearchAutoComplete -> searchAutoComplete(intent)
            is SearchAction.AddSearchHistory -> addSearchHistory(intent)
            is SearchAction.DeleteSearchHistory -> deleteSearchHistory(intent)
            is SearchAction.UpdateSearchWords -> updateState { copy(searchWords = intent.searchWords) }
            else -> Unit
        }
    }

    private suspend fun addSearchHistory(action: SearchAction.AddSearchHistory) {
        withIOContext {
            val searchWords = action.searchWords
            val search = searchHistory.value
            // add to search history if not exist
            val index = search.searchHistoryList.indexOfFirst { it.keyword == searchWords }
            searchHistory.value = if (index == -1) {
                search.copy(
                    searchHistoryList = search.searchHistoryList.toMutableList().apply {
                        add(
                            0,
                            SearchHistory(
                                keyword = searchWords,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                )
            } else {
                // move to first if exist
                val searchHistory = search.searchHistoryList[index]
                search.copy(
                    searchHistoryList = search.searchHistoryList.toMutableList().apply {
                        removeAt(index)
                        add(0, searchHistory)
                    }
                )
            }
        }
    }

    private suspend fun deleteSearchHistory(action: SearchAction.DeleteSearchHistory) {
        withIOContext {
            searchHistory.value = with(searchHistory.value) {
                val index = searchHistoryList.indexOfFirst { it.keyword == action.searchWords }
                copy(
                    searchHistoryList = searchHistoryList.toMutableList().apply {
                        removeAt(index)
                    }
                )
            }
        }
    }

    private fun searchAutoComplete(action: SearchAction.SearchAutoComplete) {
        launchIO {
            val resp = PixivRepository.searchAutoComplete(word = action.searchWords)
            updateState {
                copy(autoCompleteSearchWords = resp.tags.toImmutableList())
            }
        }
    }
}