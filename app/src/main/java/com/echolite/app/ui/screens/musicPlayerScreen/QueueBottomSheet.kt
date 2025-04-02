package com.echolite.app.ui.screens.musicPlayerScreen

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.components.VerticalSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    songList: List<SongResponseModel>,
    onClick: (SongResponseModel) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        modifier = Modifier.systemBarsPadding()
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            flingBehavior = ScrollableDefaults.flingBehavior(),
            userScrollEnabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(songList) { song ->
                SongListItem(
                    song = song,
                    isArtist = true,
                    onClick = { onClick(song) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
            item { VerticalSpace(20.dp) }
        }
    }
}