@file:JvmName("KotlinSerializationConverterFactory")

package com.mrl.pixiv.network.converter

import com.mrl.pixiv.data.Rlt
import com.mrl.pixiv.network.converter.Serializer.FromBytes
import com.mrl.pixiv.network.converter.Serializer.FromString
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class Factory(
  private val contentType: MediaType,
  private val serializer: Serializer
) : Converter.Factory() {
  @Suppress("RedundantNullableReturnType") // Retaining interface contract.
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *>? {
    val rawType = if (getRawType(type) == Flow::class.java) {
      val resultType = getParameterUpperBound(0, type as ParameterizedType)
      if (getRawType(resultType) == Rlt::class.java) {
        getParameterUpperBound(0, resultType as ParameterizedType)
      } else {
        throw IllegalArgumentException("Flow<Rlt<T>> is expected")
      }
    } else {
      getRawType(type)
    }
    val loader = serializer.serializer(rawType)
    return DeserializationStrategyConverter(loader, serializer)
  }

  @Suppress("RedundantNullableReturnType") // Retaining interface contract.
  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<out Annotation>,
    methodAnnotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<*, RequestBody>? {
    val saver = serializer.serializer(type)
    return SerializationStrategyConverter(contentType, saver, serializer)
  }
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for string-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
@JvmName("create")
fun StringFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
  return Factory(contentType, FromString(this))
}

/**
 * Return a [Converter.Factory] which uses Kotlin serialization for byte-based payloads.
 *
 * Because Kotlin serialization is so flexible in the types it supports, this converter assumes
 * that it can handle all types. If you are mixing this with something else, you must add this
 * instance last to allow the other converters a chance to see their types.
 */
@JvmName("create")
fun BinaryFormat.asConverterFactory(contentType: MediaType): Converter.Factory {
  return Factory(contentType, FromBytes(this))
}
