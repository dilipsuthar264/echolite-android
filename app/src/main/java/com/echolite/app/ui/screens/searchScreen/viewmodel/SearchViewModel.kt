package com.echolite.app.ui.screens.searchScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.data.repository.SearchRepo
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject() constructor(
    private val searchRepo: SearchRepo,
    val musicPlayerStateHolder: MusicPlayerStateHolder
) : ViewModel() {

    // * Query for all

    private val _queryModel = MutableStateFlow(QueryModel())
    val queryModel = _queryModel.asStateFlow()
    fun updateQueryModel(queryModel: QueryModel) {
        _queryModel.value = queryModel
    }

    //* search songs

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _songs: Flow<PagingData<SongResponseModel>> =
        queryModel.flatMapLatest { query ->
            searchRepo.searchSong(query)
        }.cachedIn(viewModelScope)

    fun getSongs() = _songs

    //* search Artist

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _artists: Flow<PagingData<ArtistResponseModel>> =
        queryModel.flatMapLatest { query ->
                searchRepo.searchArtist(query)
        }.cachedIn(viewModelScope)

    fun getArtists() = _artists


    //* search Album

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _albums: Flow<PagingData<AlbumResponseModel>> =
        queryModel.flatMapLatest { query ->
                searchRepo.searchAlbum(query)
        }.cachedIn(viewModelScope)

    fun getAlbum() = _albums
}