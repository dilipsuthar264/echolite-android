package com.echolite.app.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.DownloadUrlModel
import com.echolite.app.data.model.response.ImageResponseModel
import com.echolite.app.data.model.response.SongArtistsModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.room.entities.FavoriteSongEntity
import com.echolite.app.room.entities.SongEntity


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

fun SongEntity.toSongResponseModel(): SongResponseModel {
    return SongResponseModel(
        id = this.songId,
        name = this.name,
        duration = this.duration,
        album = this.albumResponseModel.fromJson<AlbumResponseModel>(),
        artists = this.songArtistsModel.fromJson<SongArtistsModel>(),
        image = this.image.asSequence().map { ImageResponseModel(url = it) }.toList(),
        downloadUrl = this.downloadUrl.asSequence().map { DownloadUrlModel(url = it) }.toList()
    )
}
fun FavoriteSongEntity.toSongResponseModel(): SongResponseModel {
    return SongResponseModel(
        id = this.songId,
        name = this.name,
        duration = this.duration,
        album = this.albumResponseModel.fromJson<AlbumResponseModel>(),
        artists = this.songArtistsModel.fromJson<SongArtistsModel>(),
        image = this.image.asSequence().map { ImageResponseModel(url = it) }.toList(),
        downloadUrl = this.downloadUrl.asSequence().map { DownloadUrlModel(url = it) }.toList()
    )
}
fun <T> List<T>.middleItem(): T? {
    if (isEmpty()) return null
    return this[size / 2]
}