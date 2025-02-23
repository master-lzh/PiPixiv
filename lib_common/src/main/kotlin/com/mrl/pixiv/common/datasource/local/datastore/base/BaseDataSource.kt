package com.mrl.pixiv.common.datasource.local.datastore.base

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mrl.pixiv.common.coroutine.launchIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent


abstract class BaseDataSource(
    private val dataStore: DataStore<Preferences>
) : KoinComponent {

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

    fun <T> remove(key: Preferences.Key<T>) {
        launchIO {
            dataStore.edit {
                it.remove(key)
            }
        }
    }

    protected fun <T> createField(key: Preferences.Key<T>, defaultValue: T) =
        Field(key, defaultValue)

    inner class Field<T>(private val key: Preferences.Key<T>, private val defaultValue: T) {
        fun get(): Flow<T> = get(key, defaultValue)
        fun set(value: T) = set(key, value)
        fun remove() = remove(key)
    }
}
