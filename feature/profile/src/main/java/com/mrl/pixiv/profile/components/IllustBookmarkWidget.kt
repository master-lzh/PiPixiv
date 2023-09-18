package com.mrl.pixiv.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common_ui.item.IllustItem
import com.mrl.pixiv.data.Illust
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val SPAN_COUNT = 3
private const val MAX_ILLUST_COUNT = 6

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun IllustBookmarkWidget(
    navHostController: NavHostController,
    illusts: List<Illust>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "插画·漫画收藏",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = "查看全部",
                    fontSize = 12.sp,
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(12.dp),
                    tint = Color.Blue
                )
            }

        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(SPAN_COUNT),
            contentPadding = PaddingValues(5.dp),
        ) {
            items(if (illusts.size > MAX_ILLUST_COUNT) MAX_ILLUST_COUNT else illusts.size, key = { illusts[it].id }) {
                val illust = illusts[it]
                IllustItem(
                    spanCount = SPAN_COUNT,
                    url = illust.imageUrls.squareMedium ?: return@items,
                    imageCount = illust.pageCount ?: 0,
                ) {
                    navHostController.navigate(
                        "${Destination.PictureScreen.route}/${
                            Base64.UrlSafe.encode(
                                Json.encodeToString(illust).encodeToByteArray()
                            )
                        }"
                    )
                }
            }
        }
    }
}