package com.mrl.pixiv.datasource.local.datastore.base

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mrl.pixiv.common.coroutine.launchIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


abstract class BaseDataSource(
    private val dataStore: DataStore<Preferences>
) : KoinComponent {
    val json by inject<Json>()

    fun <T> set(key: Preferences.Key<T>, value: T) {
        launchIO {
            dataStore.edit {
                it[key] = value
            }
        }
    }


    fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.map {
            it[key] ?: defaultValue
        }

    protected fun <T> createFiled(key: Preferences.Key<T>) = Filed(key)

    protected fun createObjectFiled(key: Preferences.Key<String>) = ObjectFiled(key)

    open inner class Filed<T>(private val key: Preferences.Key<T>) {
        open fun get(defaultValue: T): Flow<T> = get(key, defaultValue)
        open fun set(value: T) = set(key, value)
    }

    open inner class ObjectFiled(val key: Preferences.Key<String>) {
        inline fun <reified T> get(): Flow<T> = get(key, "{}").map {
            json.decodeFromString<T>(it)
        }

        inline fun <reified T> set(value: T) {
            set(key, json.encodeToString<T>(value))
        }
    }
}
