package com.echolite.app.ui.screens.artistsProfileScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.data.repository.ArtistRepo
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val artistRepo: ArtistRepo,
     val musicPlayerStateHolder: MusicPlayerStateHolder
) : ViewModel() {

    // * get artist by id

    private val _artistDetails = MutableStateFlow<BaseModel<ArtistResponseModel>>(BaseModel.idle())
    val artistDetails = _artistDetails.asStateFlow()

    fun getArtistById(artistId: String, queryModel: QueryModel) {
        viewModelScope.launch {
            _artistDetails.value = BaseModel.loading()
            _artistDetails.value = artistRepo.getArtistById(artistId, queryModel)
        }
    }

    // * Query for all

    private val _queryModel = MutableStateFlow(QueryModel(page = 0, limit = 10))
    val queryModel = _queryModel.asStateFlow()
    fun updateQueryModel(queryModel: QueryModel) {
        _queryModel.value = queryModel
    }

    private val _artistId = MutableStateFlow("")
    val artistId = _artistId.asStateFlow()
    fun updateArtistId(artistId: String) {
        _artistId.value = artistId
    }

    // * get Artist Songs
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artistSongs: Flow<PagingData<SongResponseModel>> =
        combine(_artistId, queryModel) { artistId, query ->
            artistId to query
        }.flatMapLatest { (artistId, query) ->
            artistRepo.getArtistSongs( artistId, query)
        }.cachedIn(viewModelScope)

    fun getArtistSongs() = _artistSongs

    //* Get Artist Albums
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artistAlbums: Flow<PagingData<AlbumResponseModel>> =
        combine(_artistId, queryModel) { artistId, query ->
            artistId to query
        }.flatMapLatest { (artistId, query) ->
            artistRepo.getArtistAlbum( artistId, query)
        }.cachedIn(viewModelScope)

    fun getArtistAlbums() = _artistAlbums
}
