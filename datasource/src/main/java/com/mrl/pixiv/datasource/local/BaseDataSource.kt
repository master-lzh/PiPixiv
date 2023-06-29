package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mrl.pixiv.common.data.DispatcherEnum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named


abstract class BaseDataSource(
    val dataStore: DataStore<Preferences>
) : KoinComponent {
    protected val ioDispatcher: CoroutineDispatcher by inject(named(DispatcherEnum.IO))
    protected suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }


    protected inline fun <reified T> get(key: Preferences.Key<T>): Flow<T> =
        dataStore.data.map {
            it[key] ?: when (T::class) {
                String::class -> "" as T
                Int::class -> 0 as T
                Long::class -> 0L as T
                Float::class -> 0f as T
                Boolean::class -> false as T
                Set::class -> setOf<String>() as T
                else -> throw IllegalArgumentException("Unknown type")
            }
        }.flowOn(ioDispatcher)
}
