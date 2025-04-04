package com.echolite.app.ui.screens.favoriteScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.room.entities.FavoriteSongEntity
import com.echolite.app.room.repo.FavoriteSongRepo
import com.echolite.app.utils.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepo: FavoriteSongRepo,
) : ViewModel() {


    // loading state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    /**
     *  RECENT ALBUM
     */
    fun toggleFav(song: SongResponseModel, done: () -> Unit) {
        viewModelScope.launch {
            val songEntity = FavoriteSongEntity(
                songId = song.id,
                name = song.name,
                duration = song.duration,
                albumResponseModel = song.album.toJson(),
                songArtistsModel = song.artists.toJson(),
                image = song.image?.map { it.url ?: "" }.orEmpty(),
                downloadUrl = song.downloadUrl?.map { it.url ?: "" }.orEmpty()
            )
            favoriteRepo.toggleFavorite(songEntity)
            done()
            fetchFavSong()
        }
    }

    fun removeFromFav(songId: String) {
        viewModelScope.launch {
            favoriteRepo.removeSongFromFavorite(songId)
        }
    }

    private val _fetchFavSong = MutableStateFlow<List<FavoriteSongEntity>>(emptyList())
    val fetchFavSong = _fetchFavSong.asStateFlow()

    fun fetchFavSong() {
        viewModelScope.launch {
            _isLoading.value = true
            _fetchFavSong.value = favoriteRepo.getAllFavorites()
            _isLoading.value = false
        }
    }


    private val _isFav = MutableStateFlow(false)
    val isFav = _isFav.asStateFlow()
    fun isFav(songId: String) {
        viewModelScope.launch {
            _isFav.value = favoriteRepo.isSongFavorite(songId)
        }
    }

    init {
        fetchFavSong()
    }

}