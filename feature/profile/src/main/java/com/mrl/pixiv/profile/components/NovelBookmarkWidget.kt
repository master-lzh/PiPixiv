package com.mrl.pixiv.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.data.Novel

private const val MAX_SHOW_NOVEL_COUNT = 3

@Composable
fun NovelBookmarkWidget(
    novels: List<Novel>,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = "小说收集",
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
        Divider(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 5.dp)
        )
        novels.take(MAX_SHOW_NOVEL_COUNT).forEach {
            NovelItem(it)
        }
    }
}

@Composable
private fun NovelItem(
    novel: Novel
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
        ) {
            ConstraintLayout {
                val (image, likeIcon) = createRefs()
                AsyncImage(
                    modifier = Modifier
                        .height(90.dp)
                        .padding(start = 16.dp)
                        .constrainAs(image) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(novel.imageUrls.medium)
                        .build(),
                    contentDescription = null
                )
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .constrainAs(likeIcon) {
                            top.linkTo(image.bottom)
                            start.linkTo(image.start)
                            end.linkTo(image.end)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        text = novel.totalBookmarks.toString(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }

            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                novel.series.title?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF858585),
                    )
                }
                Text(
                    text = novel.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = if (novel.series.title == null) 0.dp else 5.dp)
                )
                //author
                Text(
                    text = "by ${novel.user.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 5.dp)
                )
                //tag
                Text(
                    text = "${novel.textLength}字 ${novel.tags.joinToString(" ") { "#${it.name}" }}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 15.dp)
        )
    }

}