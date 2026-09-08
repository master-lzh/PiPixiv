package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import com.mrl.pixiv.common.data.comment.Comment
import com.mrl.pixiv.common.datasource.remote.PixivApi
import com.mrl.pixiv.common.datasource.remote.createPixivApi
import com.mrl.pixiv.common.router.CommentType
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CommentRepliesPagingSourceTest {
    @Test
    fun novelRepliesUseNovelEndpointForInitialAndNextPage() = runTest {
        checkPages(CommentType.NOVEL, "novel")
    }

    @Test
    fun illustrationRepliesKeepIllustrationEndpointForInitialAndNextPage() = runTest {
        checkPages(CommentType.ILLUST, "illust")
    }

    @Test
    fun failedRequestCanBeRetriedWithoutLosingTheParentComment() = runTest {
        var attempts = 0
        val engine = MockEngine { request ->
            assertEquals("91", request.url.parameters["comment_id"])
            attempts += 1
            respond(
                content = if (attempts == 1) "{}" else """{"comments": []}""",
                status = if (attempts == 1) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        withApi(engine) { api ->
            val source = CommentRepliesPagingSource(91, CommentType.NOVEL, api) { it }
            assertIs<PagingSource.LoadResult.Error<String, Comment>>(source.load(refresh()))
            val page = assertIs<PagingSource.LoadResult.Page<String, Comment>>(source.load(refresh()))
            assertTrue(page.data.isEmpty())
            assertNull(page.nextKey)
            assertEquals(2, attempts)
        }
    }

    @Test
    fun cancelledRequestPropagatesCancellation() = runTest {
        withApi(MockEngine { throw CancellationException("Parent comment changed") }) { api ->
            val source = CommentRepliesPagingSource(91, CommentType.NOVEL, api) { it }
            assertFailsWith<CancellationException> { source.load(refresh()) }
        }
    }

    private suspend fun checkPages(type: CommentType, path: String) {
        val cursor = "https://app-api.pixiv.net/v2/$path/comment/replies?comment_id=91&offset=5"
        var requests = 0
        val engine = MockEngine { request ->
            requests += 1
            assertEquals("/v2/$path/comment/replies", request.url.encodedPath)
            assertEquals("91", request.url.parameters["comment_id"])
            assertEquals(if (requests == 1) null else "5", request.url.parameters["offset"])
            respond(
                content = if (requests == 1) {
                    """{
                        "comments": [
                            {"id": 1, "comment": "Reply", "date": "2026-09-08T12:00:00+09:00", "user": {"id": 7}},
                            {"id": 2, "comment": "Blocked", "date": "2026-09-08T12:00:00+09:00", "user": {"id": 8}}
                        ],
                        "next_url": "$cursor"
                    }"""
                } else {
                    """{"comments": [], "next_url": ""}"""
                },
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        withApi(engine) { api ->
            val source = CommentRepliesPagingSource(91, type, api) { comments ->
                comments.filterNot { it.id == 2L }
            }
            val first = assertIs<PagingSource.LoadResult.Page<String, Comment>>(source.load(refresh()))
            assertEquals(listOf(1L), first.data.map(Comment::id))
            assertEquals(cursor, first.nextKey)
            assertNull(first.prevKey)
            val next = assertIs<PagingSource.LoadResult.Page<String, Comment>>(
                source.load(PagingSource.LoadParams.Append(cursor, 5, false)),
            )
            assertTrue(next.data.isEmpty())
            assertNull(next.prevKey)
            assertNull(next.nextKey)
            assertEquals(2, requests)
        }
    }

    private fun refresh() = PagingSource.LoadParams.Refresh<String>(null, 5, false)

    private suspend fun withApi(engine: MockEngine, block: suspend (PixivApi) -> Unit) {
        val client = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        try {
            block(Ktorfit.Builder().baseUrl("https://app-api.pixiv.net/").httpClient(client).build().createPixivApi())
        } finally {
            client.close()
            engine.close()
        }
    }
}
