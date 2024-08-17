package com.mrl.pixiv.datasource.local.datastore

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mrl.pixiv.data.search.Search
import com.mrl.pixiv.data.search.SearchSerializer
import com.mrl.pixiv.datasource.local.datastore.base.BaseProtoDataSource
import com.mrl.pixiv.datasource.local.datastore.base.dataStorePath
import okio.FileSystem
import org.koin.core.annotation.Single

@Single(binds = [SearchDataSource::class])
class SearchDataSource : BaseProtoDataSource<Search>(searchDataStore) {
    override fun defaultValue(): Search = Search.defaultInstance
}

val searchDataStore = DataStoreFactory.create(
    storage = OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = SearchSerializer,
        producePath = {
            dataStorePath("search.json")
        },
    ),
)