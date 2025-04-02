package com.echolite.app.ui.screens.searchScreen.components


import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.ui.components.PagingListLoader
import com.echolite.app.ui.components.SongListItem

@Composable
fun SongsListView(
    songs: LazyPagingItems<SongResponseModel>,
    currentTrack: SongResponseModel?,
    modifier: Modifier,
    onClick: (SongResponseModel?) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        flingBehavior = ScrollableDefaults.flingBehavior(),
        userScrollEnabled = true,
        modifier = modifier
    ) {
        items(
            songs.itemCount,
//                    key = songs.itemKey { it.id!! }
        ) { index ->
            val song = songs[index] ?: return@items
            val isPlaying = song.id == currentTrack?.id
            SongListItem(
                song.copy(isPlaying = isPlaying),
                isArtist = true,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                onClick = {
                    onClick(song)
                }
            )
        }
        songs.apply {
            item {
                PagingListLoader(loadState)
            }
        }
    }
}
