package com.echolite.app.ui.screens.searchScreen.components

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.ui.components.ArtistListItem
import com.echolite.app.ui.components.PagingListLoader

@Composable
fun ColumnScope.ArtistsListView(
    artists: LazyPagingItems<ArtistResponseModel>,
    onClick: (String) -> Unit
) {

    LazyColumn(
        state = rememberLazyListState(),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true,
        modifier = Modifier.Companion.weight(1f)
    ) {
        items(
            artists.itemCount,
//                    key = songs.itemKey { it.id!! }
        ) { index ->
            val artist = artists[index]
            ArtistListItem(artist, onClick = onClick)
        }
        artists.apply {
            item {
                PagingListLoader(loadState)
            }
        }
    }
}
