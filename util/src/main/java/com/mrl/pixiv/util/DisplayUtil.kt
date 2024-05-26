@file:Suppress("DEPRECATION")

package com.mrl.pixiv.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

object DisplayUtil {

    private const val PORTRAIT = 0
    private const val LANDSCAPE = 1

    private var realSizes = arrayOfNulls<Point>(2)

    private fun getConfiguration(): Configuration {
        return getResources().configuration
    }

    private fun getResources(): Resources {
        return AppUtil.appContext.resources
    }

    fun getScreenLayout(): Int {
        return getConfiguration().screenLayout
    }

    fun getDisplayMetrics(): DisplayMetrics {
        return getResources().displayMetrics
    }

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun sp2px(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDisplayMetrics())
            .toInt()
    }

    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        val scale: Float = getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }

    @JvmStatic
    fun getScreenHeight(): Int {
        return getDisplayMetrics().heightPixels
    }

    @Composable
    @JvmStatic
    fun getScreenHeightDp(): Dp {
        return LocalConfiguration.current.screenHeightDp.dp
    }

    @JvmStatic
    fun getScreenWidth(): Int {
        return getDisplayMetrics().widthPixels
    }

    @JvmStatic
    @Composable
    fun getScreenWidthDp(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp
    }

    @JvmStatic
    fun isLandscape(): Boolean {
        return getResources().configuration.orientation == 2
    }

    @JvmStatic
    fun isPortrait(): Boolean {
        return getResources().configuration.orientation == 1
    }

    fun getOrientation(): Int {
        return getConfiguration().orientation
    }

    @JvmStatic
    fun isRtlLayout(): Boolean {
        val lan = Locale.getDefault().language
        return "ar".equals(lan, ignoreCase = true)
    }
//
//    @JvmStatic
//    fun getOrientationInDegree(): Int {
//        val degrees = 0
//        val activity: Activity? = AppUtil.currentActivity
//        return if (activity == null) {
//            degrees
//        } else {
//            val rotation = activity.windowManager.defaultDisplay.rotation
//            val degrees: Short = when (rotation) {
//                0 -> 0
//                1 -> 90
//                2 -> 180
//                3 -> 270
//                else -> 0
//            }
//            degrees.toInt()
//        }
//    }

    fun getScreenOrientation(context: Context?): Int {
        val orientation =
            context?.resources?.configuration?.orientation ?: getConfiguration().orientation
        return if (orientation == Configuration.ORIENTATION_PORTRAIT) PORTRAIT else LANDSCAPE
    }

    fun getRealScreenSize(context: Context?): Point {
        val orientation = getScreenOrientation(context)
        var size = realSizes[orientation]
        if (size != null) {
            return size
        }

        size = Point()
        realSizes[orientation] = size
        val windowManager =
            if (context != null) {
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            } else {
                AppUtil.getSystemService(Context.WINDOW_SERVICE)
            }
        if (windowManager == null) {
            size.x = getScreenWidth()
            size.y = getScreenHeight()
        } else {
            val display = windowManager.defaultDisplay
            display.getRealSize(size)
        }
        return size
    }

    /**
     * 获取屏幕的真实宽度，横屏下包括导航栏和状态栏，通常只用于直播间SurfaceView 位置相关的计算
     *
     * @param context 当前页面的 context
     * @return 屏幕的宽度
     */
    fun getScreenRealWidth(context: Context?): Int {
        return getRealScreenSize(context).x
    }

    /**
     * 获取屏幕的真实高度，竖屏下包括导航栏和状态栏，通常只用于直播间SurfaceView 位置相关的计算
     *
     * @param context 当前页面的 context
     * @return 屏幕的高度
     */
    fun getScreenRealHeight(context: Context?): Int {
        return getRealScreenSize(context).y
    }


}