package com.mrl.pixiv.common.ui

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val lightBlue = Color(0xFF03A9F4)
val deepBlue = Color(0xFF2B7592)

val LightColors = lightColors(
    primary = lightBlue, // 天蓝色
    primaryVariant = Color(0xFF0288D1), // 深天蓝色
    secondary = Color(0xFFFFAB91), // 柔和的橙色
    secondaryVariant = Color(0xFFFF8A65), // 稍深的橙色
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

val DarkColors = darkColors(
    primary = lightBlue, // 天蓝色，与亮色主题相同
    primaryVariant = Color(0xFF0277BD), // 暗色主题下的深天蓝色
    secondary = Color(0xFFE1BEE7), // 暗色主题下的柔和紫色，作为次要色
    secondaryVariant = Color(0xFFCE93D8), // 暗色主题下的稍深紫色
    background = Color(0xFF121212), // 暗色背景，一般是非常深的灰色或黑色
    surface = Color(0xFF1E1E1E), // 暗色表面，比背景稍亮一点的灰色
    error = Color(0xFFCF6679), // 暗色主题下的错误颜色，一般是柔和的红色
    onPrimary = Color.White, // 在天蓝色背景上的文字颜色，保持白色
    onSecondary = Color.Black, // 在次要色背景上的文字颜色，保持黑色
    onBackground = Color.White, // 在暗色背景上的文字颜色，使用白色以提高可读性
    onSurface = Color.White, // 在暗色表面上的文字颜色，同样使用白色
    onError = Color.Black // 在错误颜色背景上的文字颜色，使用黑色以确保对比
)