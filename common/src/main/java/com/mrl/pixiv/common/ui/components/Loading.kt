package com.mrl.pixiv.common.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mrl.pixiv.common.R

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    isLoading: Boolean
) {
    val lottieRes = if (darkTheme) R.raw.loading_dots_dark else R.raw.loading_dots
    val lottieComp by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(lottieRes))
    val lottieProgress by animateLottieCompositionAsState(
        composition = lottieComp,
        iterations = LottieConstants.IterateForever,
    )

    AnimatedVisibility(visible = isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                modifier = modifier
                    .size(150.dp),
                composition = lottieComp,
                progress = { lottieProgress },
            )
        }
    }
}