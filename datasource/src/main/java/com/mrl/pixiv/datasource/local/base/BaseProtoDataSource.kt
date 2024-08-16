package com.mrl.pixiv.datasource.local.base

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.protobuf.GeneratedMessageLite
import com.mrl.pixiv.common.coroutine.launchIO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import java.io.IOException

abstract class BaseProtoDataSource<T : GeneratedMessageLite<T, TBuilder>, TBuilder : GeneratedMessageLite.Builder<T, TBuilder>>(
    private val dataStore: DataStore<T>
) : KoinComponent {

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