package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.common.data.IError
import com.mrl.pixiv.common.data.Rlt
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

suspend inline fun <reified T> HttpClient.safeGet(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): Flow<Rlt<T>> {
    try {
        val resp = get(urlString, block).body<T>()
        return flowOf(Rlt.Success(resp))
    } catch (e: Exception) {
        return flowOf(Rlt.Failed(object : IError(e) {}))
    }
}

suspend inline fun <reified T> HttpClient.safePost(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): Flow<Rlt<T>> {
    try {
        val resp = post(urlString, block).body<T>()
        return flowOf(Rlt.Success(resp))
    } catch (e: Exception) {
        return flowOf(Rlt.Failed(object : IError(e) {}))
    }
}

suspend inline fun <reified T> HttpClient.safePostForm(
    urlString: String,
    formParameters: Parameters,
    noinline block: HttpRequestBuilder.() -> Unit = {}
): Flow<Rlt<T>> {
    try {
        val resp = submitForm(urlString, formParameters, false, block).body<T>()
        return flowOf(Rlt.Success(resp))
    } catch (e: Exception) {
        return flowOf(Rlt.Failed(object : IError(e) {}))
    }
}