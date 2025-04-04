package com.echolite.app.room.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import com.echolite.app.room.entities.AlbumEntity
import com.echolite.app.room.entities.ArtistEntity
import com.echolite.app.room.entities.SongEntity
import com.echolite.app.room.repo.RecentAlbumRepo
import com.echolite.app.room.repo.RecentArtistRepo
import com.echolite.app.room.repo.RecentSongRepo
import com.echolite.app.utils.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val recentAlbumRepo: RecentAlbumRepo,
    private val recentArtistRepo: RecentArtistRepo,
    private val recentSongRepo: RecentSongRepo,
    val musicPlayerStateHolder: MusicPlayerStateHolder
) : ViewModel() {

    // loading state

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    /**
     *  RECENT ALBUM
     */
    fun addToRecentAlbum(album: AlbumResponseModel) {
        viewModelScope.launch {
            val albumEntity = AlbumEntity(
                albumId = album.id,
                name = album.name,
                image = album.image?.map { it.url ?: "" }.orEmpty(),
            )
            recentAlbumRepo.addToRecentPlayed(albumEntity)
            fetchRecentPlayedAlbums()
        }
    }

    fun removeFromRecentAlbum(albumId: String) {
        viewModelScope.launch {
            recentAlbumRepo.removeFromRecentPlayed(albumId)
            fetchRecentPlayedAlbums()
        }
    }

    private val _recentPlayedAlbums = MutableStateFlow<List<AlbumEntity>>(emptyList())
    val recentPlayedAlbums = _recentPlayedAlbums.asStateFlow()
    private fun fetchRecentPlayedAlbums() {
        viewModelScope.launch {
            _recentPlayedAlbums.value = recentAlbumRepo.getRecentPlayedAlbums()
            _isLoading.value = false
        }
    }

    /**
     *  RECENT ARTIST
     */
    fun addToRecentArtist(artist: ArtistResponseModel) {
        viewModelScope.launch {
            val artistEntity = ArtistEntity(
                artistId = artist.id,
                name = artist.name,
                image = artist.image?.map { it.url ?: "" }.orEmpty()
            )
            recentArtistRepo.addToRecentPlayed(artistEntity)
            fetchRecentPlayedArtists()
        }
    }

    fun removeFromRecentArtist(artistId: String) {
        viewModelScope.launch {
            recentArtistRepo.removeFromRecentPlayed(artistId)
            fetchRecentPlayedArtists()
        }
    }

    private val _recentPlayedArtists = MutableStateFlow<List<ArtistEntity>>(emptyList())
    val recentPlayedArtists = _recentPlayedArtists.asStateFlow()
    private fun fetchRecentPlayedArtists() {
        viewModelScope.launch {
            _recentPlayedArtists.value = recentArtistRepo.getRecentPlayedArtists()
            _isLoading.value = false
        }
    }

    /**
     *  RECENT SONG
     */

    fun addToRecentSong(song: SongResponseModel) {
        viewModelScope.launch {
            val songEntity = SongEntity(
                songId = song.id,
                name = song.name,
                duration = song.duration,
                albumResponseModel = song.album.toJson(),
                songArtistsModel = song.artists.toJson(),
                image = song.image?.map { it.url ?: "" }.orEmpty(),
                downloadUrl = song.downloadUrl?.map { it.url ?: "" }.orEmpty()
            )
            recentSongRepo.addToRecentPlayed(songEntity)
            fetchRecentPlayedSongs()
        }
    }

    fun removeFromRecentSong(songId: String) {
        viewModelScope.launch {
            recentSongRepo.removeFromRecentPlayed(songId)
            fetchRecentPlayedSongs()
        }
    }

    private val _recentPlayedSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val recentPlayedSongs = _recentPlayedSongs.asStateFlow()
    private fun fetchRecentPlayedSongs() {
        viewModelScope.launch {
            _isLoading.value= true
            _recentPlayedSongs.value = recentSongRepo.getRecentPlayedSongs()
            _isLoading.value = false
        }
    }


//    init {
//        fetchData()
//    }

    fun fetchData() {
        fetchRecentPlayedAlbums()
        fetchRecentPlayedArtists()
        fetchRecentPlayedSongs()
    }

}