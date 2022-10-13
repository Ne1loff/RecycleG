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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.data.garbage.impl.paper
import com.example.recycleg.data.garbage.impl.plastic
import com.example.recycleg.model.GarbageInfo
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.theme.RecycleGTheme

@Composable
private fun GarbageCard(
    garbageInfo: GarbageInfo,
    navigateToArticle: (GarbageType) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToArticle(garbageInfo.type) })
            .clip(shape = RoundedCornerShape(16.dp))
    ) {
        CardImage(garbage = garbageInfo, Modifier.padding(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            CardTitle(garbageInfo = garbageInfo)
            CardSubtitle(garbageInfo = garbageInfo)
        }
    }
}

@Composable
private fun ReducedGarbageCard(
    garbageInfo: GarbageInfo,
    navigateToArticle: (GarbageType) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToArticle(garbageInfo.type) })
            .width(150.dp)
            .height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardTitle(garbageInfo = garbageInfo)
            Spacer(modifier = Modifier.height(4.dp))
            CardImage(garbage = garbageInfo)
        }
    }
}

@Composable
private fun CardImage(garbage: GarbageInfo, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = garbage.imageId),
        contentDescription = null,
        modifier = modifier
            .size(64.dp, 64.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun CardTitle(garbageInfo: GarbageInfo) {
    Text(
        text = garbageInfo.title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun CardSubtitle(garbageInfo: GarbageInfo) {
    garbageInfo.subtitle?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview("Simple post card")
@Preview("Simple post card (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GarbageCardPreview() {
    RecycleGTheme {
        Surface(modifier = Modifier.clip(shape = RoundedCornerShape(16.0.dp))) {
            GarbageCard(garbageInfo = plastic, navigateToArticle = {})
        }
    }
}

@Preview("Reduced post card")
@Preview("Reduced post card (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReducedGarbageCardPreview() {
    RecycleGTheme {
        Surface(modifier = Modifier.clip(shape = RoundedCornerShape(16.0.dp))) {
            ReducedGarbageCard(garbageInfo = paper, navigateToArticle = {})
        }
    }
}