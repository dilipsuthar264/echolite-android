package com.echolite.app.ui.screens.searchScreen.components

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.ui.components.CustomListItem
import com.echolite.app.ui.components.PagingListLoader
import com.echolite.app.utils.singleClick

@Composable
fun AlbumListView(
    albums: LazyPagingItems<AlbumResponseModel>,
    modifier: Modifier,
    onClick: (String?) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true,
        modifier = modifier
    ) {
        items(
            albums.itemCount,
//                    key = songs.itemKey { it.id!! }
        ) { index ->
            val album = albums[index]
            CustomListItem(
                title = album?.name ?: "",
                titleStyle = TextStyle(
                    fontSize = 16.sp
                ),
                subtitleStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                subtitle = album?.artists?.all?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                leadingContent = {
                    AsyncImage(
                        model = album?.image?.get(1)?.url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                    )
                },
                onClick = singleClick {
                    onClick(album?.id)
                }
            )
        }
        albums.apply {
            item {
                PagingListLoader(loadState)
            }
        }
    }
}
