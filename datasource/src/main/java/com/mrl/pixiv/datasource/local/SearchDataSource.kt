package com.mrl.pixiv.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.mrl.pixiv.data.search.Search
import com.mrl.pixiv.data.search.SearchSerializer
import com.mrl.pixiv.datasource.local.base.BaseProtoDataSource

class SearchDataSource(
    searchDataSource: DataStore<Search>,
) : BaseProtoDataSource<Search, Search.Builder>(searchDataSource) {
    override fun defaultValue(): Search = Search.getDefaultInstance()
}

val Context.searchDataStore by dataStore(
    fileName = "search.pb",
    serializer = SearchSerializer
)