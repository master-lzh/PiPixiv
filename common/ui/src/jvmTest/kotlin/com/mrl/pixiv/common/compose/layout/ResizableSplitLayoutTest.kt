package com.mrl.pixiv.common.compose.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
class ResizableSplitLayoutTest {
    @Test
    fun mouseDragStopsAtBothPaneMinimums() = runDesktopComposeUiTest(
        width = 1224,
        height = 760,
        testTimeout = 30.seconds,
    ) {
        setSplitContent(SplitPaneState())
        onNodeWithTag("split-source").assertWidthIsEqualTo(504.dp)
        onNodeWithTag("split-detail").assertWidthIsEqualTo(696.dp)
        savePreview("split-default.png")

        onNodeWithTag("split-divider").performMouseInput {
            moveTo(center)
            press()
            moveBy(Offset(-360f, 0f))
            release()
        }
        onNodeWithTag("split-source").assertWidthIsEqualTo(360.dp)
        onNodeWithTag("split-detail").assertWidthIsEqualTo(840.dp)

        onNodeWithTag("split-divider").performMouseInput {
            moveTo(center)
            press()
            moveBy(Offset(720f, 0f))
            release()
        }
        onNodeWithTag("split-source").assertWidthIsEqualTo(780.dp)
        onNodeWithTag("split-detail").assertWidthIsEqualTo(420.dp)
        savePreview("split-resized.png")
    }

    @Test
    fun keyboardAdjustsFocusedDividerAndEnterResetsIt() = runDesktopComposeUiTest(
        width = 1024,
        height = 760,
        testTimeout = 30.seconds,
    ) {
        var dividerFocused = false
        setSplitContent(
            state = SplitPaneState(),
            onDividerFocusChanged = { dividerFocused = it },
        )
        val divider = onNodeWithTag("split-divider")
        divider.performMouseInput { click() }
        divider.assertIsFocused()
        runOnIdle { assertTrue(dividerFocused) }

        divider.performKeyInput { pressKey(Key.DirectionRight) }
        onNodeWithTag("split-source").assertWidthIsEqualTo(444.dp)
        divider.performKeyInput { pressKey(Key.DirectionLeft) }
        onNodeWithTag("split-source").assertWidthIsEqualTo(420.dp)
        divider.performKeyInput { pressKey(Key.MoveHome) }
        onNodeWithTag("split-source").assertWidthIsEqualTo(360.dp)
        divider.performKeyInput { pressKey(Key.MoveEnd) }
        onNodeWithTag("split-detail").assertWidthIsEqualTo(420.dp)
        divider.performKeyInput { pressKey(Key.Enter) }
        onNodeWithTag("split-source").assertWidthIsEqualTo(420.dp)
    }

    @Test
    fun touchDragAndDoubleTapUseTheSameSavedPreference() = runDesktopComposeUiTest(
        width = 1024,
        height = 760,
        testTimeout = 30.seconds,
    ) {
        val state = SplitPaneState()
        setSplitContent(state)
        onNodeWithTag("split-divider").performTouchInput {
            swipe(center, center + Offset(260f, 0f))
        }
        onNodeWithTag("split-detail").assertWidthIsEqualTo(420.dp)
        onNodeWithTag("split-divider").performTouchInput { doubleClick() }
        onNodeWithTag("split-source").assertWidthIsEqualTo(420.dp)
        runOnIdle { assertEquals(0.42f, state.fraction) }
    }

    @Test
    fun accessibilityProgressCannotShrinkEitherPaneBelowItsMinimum() = runDesktopComposeUiTest(
        width = 1024,
        height = 760,
        testTimeout = 30.seconds,
    ) {
        setSplitContent(SplitPaneState())
        val divider = onNodeWithTag("split-divider")
        divider.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            assertTrue(setProgress(-1f))
        }
        onNodeWithTag("split-source").assertWidthIsEqualTo(360.dp)
        divider.performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            assertTrue(setProgress(2f))
            assertFalse(setProgress(Float.NaN))
        }
        onNodeWithTag("split-detail").assertWidthIsEqualTo(420.dp)
    }

    @Test
    fun narrowingAndRestoringTheContainerPreservesTheUsersRatio() = runDesktopComposeUiTest(
        width = 1624,
        height = 760,
        testTimeout = 30.seconds,
    ) {
        val state = SplitPaneState().apply { updateFraction(0.65f) }
        var containerWidth by mutableStateOf(1624.dp)
        setSplitContent(state, containerWidth = { containerWidth })
        onNodeWithTag("split-source").assertWidthIsEqualTo(1040.dp)

        runOnIdle { containerWidth = 824.dp }
        onNodeWithTag("split-source").assertWidthIsEqualTo(380.dp)
        onNodeWithTag("split-detail").assertWidthIsEqualTo(420.dp)
        runOnIdle {
            assertEquals(0.65f, state.fraction)
            containerWidth = 1624.dp
        }

        onNodeWithTag("split-source").assertWidthIsEqualTo(1040.dp)
        runOnIdle { assertEquals(0.65f, state.fraction) }
    }

    private fun DesktopComposeUiTest.setSplitContent(
        state: SplitPaneState,
        containerWidth: (() -> Dp)? = null,
        onDividerFocusChanged: (Boolean) -> Unit = {},
    ) {
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize()) {
                        ResizableSplitLayout(
                            state = state,
                            minSourceWidth = 360.dp,
                            minDetailWidth = 420.dp,
                            modifier = containerWidth?.let { Modifier.width(it()) } ?: Modifier,
                            onDividerFocusChanged = onDividerFocusChanged,
                            source = { PreviewSettingsList() },
                            detail = { PreviewSettingsDetail() },
                        )
                    }
                }
            }
        }
    }

    /** These are component previews with sample content, not screenshots of the app's settings. */
    private fun DesktopComposeUiTest.savePreview(name: String) {
        val directory = File("build/reports/split-pane-preview").apply { mkdirs() }
        ImageIO.write(captureToImage().toAwtImage(), "png", File(directory, name))
    }
}

@Composable
private fun PreviewSettingsList() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("PiPixiv", style = MaterialTheme.typography.labelLarge)
            Text("设置", style = MaterialTheme.typography.headlineLarge)
            Text(
                "双栏组件预览",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp),
            )
            listOf("账号", "浏览", "显示", "下载", "关于").forEach { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (title == "浏览") MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(18.dp),
                        )
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun PreviewSettingsDetail() {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("浏览设置", style = MaterialTheme.typography.headlineMedium)
        Text(
            "拖拽中间胶囊调整左右宽度。双击可恢复默认比例。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        listOf("显示作品信息", "保留浏览位置", "自动加载下一页").forEach { title ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Switch(checked = title != "自动加载下一页", onCheckedChange = {})
            }
        }
    }
}
