package com.mrl.pixiv.common.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val lightBlue = Color(0xFF03A9F4)
val deepBlue = Color(0xFF2B7592)

val LightColors = lightColorScheme(
    primary = lightBlue, // 天蓝色
    onPrimary = Color.White, // 对于主色，文字使用白色
    primaryContainer = Color(0xFF0288D1), // 深天蓝色，作为主色容器
    onPrimaryContainer = Color.Black, // 对于主色容器，文字使用黑色
    secondary = Color(0xFFFFAB91), // 柔和的橙色
    onSecondary = Color.Black, // 对于次要色，文字使用黑色
    secondaryContainer = Color(0xFFFF8A65), // 稍深的橙色，作为次要色容器
    onSecondaryContainer = Color.Black, // 对于次要色容器，文字使用黑色
    background = Color.White, // 背景色
    onBackground = Color.Black, // 对于背景色，文字使用黑色
    surface = Color.White, // 表面色
    onSurface = Color.Black, // 对于表面色，文字使用黑色
    error = Color(0xFFB00020), // 错误色
    onError = Color.White, // 对于错误色，文字使用白色
    // Material 3 中新增的颜色字段，根据实际需求补充
    surfaceVariant = Color.LightGray, // 表面变体色
    onSurfaceVariant = Color.DarkGray, // 对于表面变体色，文字使用深灰色
)

val DarkColors = darkColorScheme(
    primary = lightBlue, // 天蓝色
    onPrimary = Color.White, // 对于主色，文字使用白色
    primaryContainer = Color(0xFF0277BD), // 暗色主题下的深天蓝色，作为主色容器
    onPrimaryContainer = Color.Black, // 对于主色容器，文字使用黑色
    secondary = Color(0xFFE1BEE7), // 暗色主题下的柔和紫色
    onSecondary = Color.Black, // 对于次要色，文字使用黑色
    secondaryContainer = Color(0xFFCE93D8), // 暗色主题下的稍深紫色，作为次要色容器
    onSecondaryContainer = Color.Black, // 对于次要色容器，文字使用黑色
    background = Color(0xFF121212), // 暗色背景
    onBackground = Color.White, // 对于暗色背景，文字使用白色
    surface = Color(0xFF1E1E1E), // 暗色表面
    onSurface = Color.White, // 对于暗色表面，文字使用白色
    error = Color(0xFFCF6679), // 暗色主题下的错误颜色
    onError = Color.Black, // 对于错误色，文字使用黑色
    // Material 3 中新增的颜色字段，根据实际需求补充
    surfaceVariant = Color.Gray, // 表面变体色
    onSurfaceVariant = Color.LightGray, // 对于表面变体色，文字使用浅灰色
    // 其他颜色根据项目需求自行添加
)