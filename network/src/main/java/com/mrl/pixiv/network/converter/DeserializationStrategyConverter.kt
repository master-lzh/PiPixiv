package com.mrl.pixiv.network.converter

import com.mrl.pixiv.common.data.NetworkHttpParseError
import com.mrl.pixiv.common.data.NetworkHttpResBodyNullError
import com.mrl.pixiv.common.data.Rlt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.DeserializationStrategy
import okhttp3.ResponseBody
import retrofit2.Converter

internal class DeserializationStrategyConverter<T>(
    private val loader: DeserializationStrategy<T>,
    private val serializer: Serializer
) : Converter<ResponseBody, Flow<Rlt<T>>> {
    override fun convert(value: ResponseBody): Flow<Rlt<T>> {
        if (value.contentLength() == 0L) {
            return flow { emit(Rlt.Failed(NetworkHttpResBodyNullError(Exception("response body is null")))) }
        }
        return flow {
            emit(
                try {
                    Rlt.Success(serializer.fromResponseBody(loader, value))
                } catch (e: Exception) {
                    Rlt.Failed(NetworkHttpParseError(e))
                }
            )
        }
    }
}
