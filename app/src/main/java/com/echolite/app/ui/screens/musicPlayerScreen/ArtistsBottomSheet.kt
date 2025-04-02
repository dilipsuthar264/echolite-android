package com.echolite.app.ui.screens.musicPlayerScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.ui.components.ArtistListItem
import com.echolite.app.ui.components.VerticalSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsBottomSheet(
    artist: List<ArtistResponseModel>,
    onClick: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        modifier = Modifier.systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(artist){
                ArtistListItem(artist = it, onClick = onClick)

            }
            item {
                VerticalSpace(20.dp)
            }
        }
    }
}