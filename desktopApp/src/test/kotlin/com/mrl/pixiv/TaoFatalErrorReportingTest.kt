package com.mrl.pixiv

import java.util.logging.Level
import java.util.logging.LogRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TaoFatalErrorReportingTest {
    @Test
    fun reportsBothKnownFatalExits() {
        for (message in fatalMessages) {
            val reported = mutableListOf<Throwable>()
            val error = IllegalStateException("Test failure")
            val handler = TaoFatalErrorHandler(loggerName, reported::add)

            handler.publish(record(message, error))

            assertEquals(1, reported.size)
            assertSame(error, reported.single())
        }
    }

    @Test
    fun ignoresRecoverableLogsOtherLoggersAndShutdownFailures() {
        val reported = mutableListOf<Throwable>()
        val handler = TaoFatalErrorHandler(loggerName, reported::add)
        val error = IllegalStateException("Test failure")

        handler.publish(record("Recoverable rendering warning", error))
        handler.publish(record("Unhandled exception on the Tao main thread while shutting down", error))
        handler.publish(record(fatalMessages.first(), error).apply { loggerName = "unrelated" })
        handler.publish(record(fatalMessages.first(), error).apply { level = Level.WARNING })
        handler.publish(record(fatalMessages.first(), null))
        handler.publish(null)

        assertEquals(0, reported.size)
        // Filtering must not consume the first-fatal slot.
        handler.publish(record(fatalMessages.first(), error))
        assertEquals(1, reported.size)
        assertSame(error, reported.single())
    }

    @Test
    fun reportsOnlyFirstFatalAndDoesNotInterruptExitWhenReportingFails() {
        var reportCount = 0
        val handler = TaoFatalErrorHandler(loggerName) {
            reportCount++
            throw IllegalStateException("Reporting unavailable")
        }
        val error = IllegalStateException("Test failure")

        handler.publish(record(fatalMessages.first(), error))
        handler.publish(record(fatalMessages.first(), error))
        handler.publish(record(fatalMessages.last(), IllegalStateException("Follow-up failure")))

        assertEquals(1, reportCount)
    }

    private fun record(message: String, error: Throwable?): LogRecord =
        LogRecord(Level.SEVERE, message).apply {
            loggerName = Companion.loggerName
            thrown = error
        }

    private companion object {
        const val loggerName = "test.tao"
        val fatalMessages = listOf(
            "Unhandled exception on the Tao main thread — closing",
            "taoApplication failed",
        )
    }
}
