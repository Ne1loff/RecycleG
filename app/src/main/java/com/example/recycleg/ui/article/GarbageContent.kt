package com.example.recycleg.ui.article

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recycleg.model.GarbageInfo

private val defaultSpacerSize = 16.dp

@Composable
fun GarbageContent(
    garbageInfo: GarbageInfo,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        contentPadding = PaddingValues(defaultSpacerSize),
        modifier = modifier,
        state = state
    ) {
        garbageContentItem(garbageInfo)
    }
}

fun LazyListScope.garbageContentItem(garbageInfo: GarbageInfo) {
    item {
        // TODO:
    }
}
