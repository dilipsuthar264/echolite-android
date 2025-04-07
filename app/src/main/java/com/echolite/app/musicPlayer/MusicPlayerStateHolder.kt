package com.echolite.app.musicPlayer

import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.room.entities.FavoriteSongEntity
import com.echolite.app.room.entities.SongEntity
import com.echolite.app.utils.MusicRepeatMode
import com.echolite.app.utils.toSongResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerStateHolder @Inject constructor() {

    private val _isPlaying = MutableStateFlow<Boolean>(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _maxDuration = MutableStateFlow(0f)
    val maxDuration = _maxDuration.asStateFlow()

    private val _currentDuration = MutableStateFlow(0f)
    val currentDuration = _currentDuration.asStateFlow()

    private val _currentTrack = MutableStateFlow<SongResponseModel?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _songList = MutableStateFlow<List<SongResponseModel>>(emptyList())
    val songList = _songList.asStateFlow()

    private val _repeatMode = MutableStateFlow<MusicRepeatMode>(MusicRepeatMode.REPEAT)
    val repeatMode = _repeatMode.asStateFlow()

    fun updateIsPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun updateLoading(isLoading: Boolean) {
        _isLoading.update { isLoading }
    }

    fun updateMaxDuration(duration: Float) {
        _maxDuration.value = duration
    }

    fun updateCurrentDuration(duration: Float) {
        _currentDuration.value = duration
    }

    fun updateCurrentTrack(song: SongResponseModel?) {
        _currentTrack.value = song
    }

    fun updateSongList(songList: List<SongResponseModel>) {
        _songList.update { songList }
    }

    fun updateRepeatMode(mode: MusicRepeatMode) {
        _repeatMode.update { mode }
    }


    private val _commands = MutableSharedFlow<MusicCommand>()
    val commands = _commands.asSharedFlow()

    fun toggleRepeat() {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.ToggleRepeat)
        }
    }

    fun playPause() {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.PlayPause)
        }
    }

    fun next() {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.Next)
        }
    }

    fun prev() {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.Previous)
        }
    }

    fun playTrack(song: SongResponseModel, songList: List<SongResponseModel>) {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.PlayTrack(song, songList))
        }
    }


    fun playTrack(song: SongEntity, songList: List<SongEntity>) {
        CoroutineScope(Dispatchers.Main).launch {
            val mappedSong = song.toSongResponseModel()
            val mappedList = songList.asSequence().map { it.toSongResponseModel() }.toList()
            _commands.emit(MusicCommand.PlayTrack(mappedSong, mappedList))
        }
    }


    fun playTrack(song: FavoriteSongEntity, songList: List<FavoriteSongEntity>) {
        CoroutineScope(Dispatchers.Main).launch {
            val mappedSong = song.toSongResponseModel()
            val mappedList = songList.asSequence().map { it.toSongResponseModel() }.toList()
            _commands.emit(MusicCommand.PlayTrack(mappedSong, mappedList))
        }
    }

    fun seekTo(position: Float) {
        CoroutineScope(Dispatchers.Main).launch {
            _commands.emit(MusicCommand.SeekTo(position))
        }
    }

}

sealed class MusicCommand() {
    data class PlayTrack(
        val song: SongResponseModel,
        val songList: List<SongResponseModel>
    ) : MusicCommand()

    data class SeekTo(val position: Float) : MusicCommand()
    data object PlayPause : MusicCommand()
    data object Next : MusicCommand()
    data object Previous : MusicCommand()
    data object ToggleRepeat : MusicCommand()
}

