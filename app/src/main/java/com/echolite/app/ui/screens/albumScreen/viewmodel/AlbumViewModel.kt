package com.echolite.app.ui.screens.albumScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.repository.AlbumRepo
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepo: AlbumRepo,
    val musicPlayerStateHolder: MusicPlayerStateHolder
) : ViewModel() {

    // * get album details by id

    private val _albumDetails = MutableStateFlow<BaseModel<AlbumResponseModel>>(BaseModel.idle())
    val albumDetails = _albumDetails.asStateFlow()

    fun getAlbumById(albumId: String) {
        viewModelScope.launch {
            _albumDetails.value = BaseModel.loading()
            _albumDetails.value = albumRepo.getAlbumById(albumId)
        }
    }


}