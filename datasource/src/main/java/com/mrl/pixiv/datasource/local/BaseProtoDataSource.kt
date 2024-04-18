package com.mrl.pixiv.datasource.local

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.protobuf.GeneratedMessageLite
import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.data.DispatcherEnum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.IOException

abstract class BaseProtoDataSource<T : GeneratedMessageLite<T, TBuilder>, TBuilder : GeneratedMessageLite.Builder<T, TBuilder>>(
    private val dataStore: DataStore<T>
) : KoinComponent {
    private val ioDispatcher: CoroutineDispatcher by inject(named(DispatcherEnum.IO))

    val data = dataStore.data.catch {
        if (it is IOException) {
            Log.e(this@BaseProtoDataSource::class.java.simpleName, ": ", it)
            emit(defaultValue())
        } else {
            throw it
        }
    }

    val syncData: T
        get() = runBlocking { data.first() }

    fun updateData(update: (T) -> T) {
        launchIO {
            dataStore.updateData {
                update(it)
            }
        }
    }

    abstract fun defaultValue(): T
}