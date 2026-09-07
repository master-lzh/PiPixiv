package com.mrl.pixiv.common.util

import androidx.compose.ui.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class ConditionallyTest {
    @Test
    fun enabledConditionAppendsEachModifierOnceInOrder() {
        val layout = TestElement("layout")
        val sharedElement = TestElement("sharedElement")
        val click = TestElement("click")
        val rightClick = TestElement("rightClick")

        val result = Modifier
            .then(layout)
            .conditionally(true) { then(sharedElement) }
            .then(click)
            .conditionally(true) { then(rightClick) }

        assertEquals(listOf(layout, sharedElement, click, rightClick), result.elements())
    }

    @Test
    fun independentlyCreatedModifierPreservesTheExistingChain() {
        val click = TestElement("click")
        val background = TestElement("background")

        val result = Modifier.then(click).conditionally(true) {
            Modifier.then(background)
        }

        assertEquals(listOf(click, background), result.elements())
    }

    @Test
    fun disabledConditionKeepsTheOriginalModifierWithoutCallingTheBlock() {
        val original = Modifier.then(TestElement("layout"))
        var blockCalled = false

        val result = original.conditionally(false) {
            blockCalled = true
            then(TestElement("click"))
        }

        assertSame(original, result)
        assertFalse(blockCalled)
    }

    @Test
    fun emptyConditionalAdditionDoesNotDuplicateTheExistingChain() {
        val layout = TestElement("layout")

        val result = Modifier.then(layout).conditionally(true) { this }

        assertEquals(listOf(layout), result.elements())
    }

    private data class TestElement(val name: String) : Modifier.Element

    private fun Modifier.elements(): List<Modifier.Element> =
        foldIn(emptyList()) { elements, element -> elements + element }
}
