package com.mrl.pixiv.network.converter

import kotlinx.serialization.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

internal sealed class Serializer {
  abstract fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T
  abstract fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody

  protected abstract val format: SerialFormat

  fun serializer(type: Type): KSerializer<Any> = format.serializersModule.serializer(type)

  class FromString(override val format: StringFormat) : Serializer() {
    override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
      val string = body.string()
      return format.decodeFromString(loader, string)
    }

    override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
      val string = format.encodeToString(saver, value)
      return string.toRequestBody(contentType)
    }
  }

  class FromBytes(override val format: BinaryFormat) : Serializer() {
    override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
      val bytes = body.bytes()
      return format.decodeFromByteArray(loader, bytes)
    }

    override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
      val bytes = format.encodeToByteArray(saver, value)
      return bytes.toRequestBody(contentType, 0, bytes.size)
    }
  }
}
