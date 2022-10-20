package com.example.recycleg.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.R
import com.example.recycleg.data.garbage.impl.paper
import com.example.recycleg.data.garbage.impl.plastic
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.theme.Black

@Composable
private fun getColorFromGarbageType(garbageInfoPost: GarbageInfoPost): Color =
    when (garbageInfoPost.type) {
        GarbageType.Paper -> colorResource(id = R.color.rg_paper_garbage)
        GarbageType.Glass -> colorResource(id = R.color.rg_glass_garbage)
        GarbageType.Metal -> colorResource(id = R.color.rg_metal_garbage)
        GarbageType.Organic -> colorResource(id = R.color.rg_organic_garbage)
        GarbageType.Plastic -> colorResource(id = R.color.rg_plastic_garbage)
    }

@Composable
fun GarbageCard(
    garbageInfoPost: GarbageInfoPost,
    navigateToArticle: (GarbageType) -> Unit
) {
    Surface(
        modifier = Modifier.clip(shape = RoundedCornerShape(16.0.dp)),
        color = getColorFromGarbageType(garbageInfoPost)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { navigateToArticle(garbageInfoPost.type) })
                .clip(shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            CardImage(garbage = garbageInfoPost, Modifier.padding(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            ) {
                CardTitle(garbageInfoPost = garbageInfoPost)
                CardSubtitle(garbageInfoPost = garbageInfoPost)
            }
        }
    }
}

@Composable
fun ReducedGarbageCard(
    garbageInfoPost: GarbageInfoPost,
    navigateToArticle: (GarbageType) -> Unit
) {
    Surface(
        modifier = Modifier.clip(shape = RoundedCornerShape(16.0.dp)),
        color = getColorFromGarbageType(garbageInfoPost)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { navigateToArticle(garbageInfoPost.type) })
                .width(150.dp)
                .height(130.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardTitle(garbageInfoPost = garbageInfoPost)
                Spacer(modifier = Modifier.height(4.dp))
                CardImage(garbage = garbageInfoPost)
            }
        }
    }
}

@Composable
private fun CardImage(garbage: GarbageInfoPost, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = garbage.imageId),
        contentDescription = null,
        modifier = modifier
            .size(64.dp, 64.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun CardTitle(garbageInfoPost: GarbageInfoPost) {
    Text(
        text = garbageInfoPost.title,
        style = MaterialTheme.typography.titleMedium,
        color = Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun CardSubtitle(garbageInfoPost: GarbageInfoPost) {
    garbageInfoPost.subtitle?.let {
        Text(
            text = it,
            color = Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview("Simple post card")
@Preview("Simple post card (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GarbageCardPreview() {
    GarbageCard(garbageInfoPost = plastic, navigateToArticle = {})
}

@Preview("Reduced post card")
@Preview("Reduced post card (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReducedGarbageCardPreview() {
    ReducedGarbageCard(garbageInfoPost = paper, navigateToArticle = {})
}