package com.echolite.app.utils

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.echolite.app.data.model.response.SongResponseModel


fun NavHostController.getViewModelStoreOwner() = this.getViewModelStoreOwner(this.graph.id)
fun NavController.getViewModelStoreOwner() = this.getViewModelStoreOwner(this.graph.id)

fun Modifier.dynamicImePadding(paddingValues: PaddingValues): Modifier {
    return this.then(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .navigationBarsPadding()
            .imePadding()
    )
}

fun Modifier.dynamicPadding(paddingValues: PaddingValues): Modifier {
    return this.then(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .navigationBarsPadding()
    )
}
fun List<SongResponseModel>.updatePlayingSong(currentTrack: SongResponseModel?): List<SongResponseModel> {
    return this.map { it.copy(isPlaying = it.id == currentTrack?.id) }
}