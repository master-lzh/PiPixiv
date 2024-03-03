package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import com.mrl.pixiv.data.search.Search

class SearchDataSource(
    searchDataSource: DataStore<Search>,
) : BaseProtoDataSource<Search, Search.Builder>(searchDataSource) {
    override fun defaultValue(): Search = Search.getDefaultInstance()
}