package com.echolite.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

@Composable
fun PagingListLoader(loadState: CombinedLoadStates) {
    when {
        loadState.refresh is LoadState.Loading -> ShowBottomLoader()
        loadState.append is LoadState.Loading -> ShowBottomLoader()
        loadState.prepend is LoadState.Loading -> ShowBottomLoader()
        else -> Unit
    }
}

@Composable
fun ShowBottomLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            strokeCap = StrokeCap.Round,
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
