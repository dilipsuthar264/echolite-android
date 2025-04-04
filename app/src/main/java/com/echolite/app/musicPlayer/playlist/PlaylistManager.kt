package com.echolite.app.musicPlayer.playlist

import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.debouncer.Debouncer
import com.echolite.app.utils.MusicRepeatMode
import com.echolite.app.utils.MusicServiceConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistManager @Inject constructor() {
    val currentTrack = MutableStateFlow<SongResponseModel?>(null)
    val musicList = MutableStateFlow<List<SongResponseModel>>(emptyList())
    val repeatMode = MutableStateFlow(MusicRepeatMode.REPEAT)

    private val originalMusicList = mutableListOf<SongResponseModel>()

    fun setList(songList: List<SongResponseModel>) {
        originalMusicList.clear()
        originalMusicList.addAll(songList)
        musicList.update { songList }
        updatePlayListForRepeatMode()
    }

    fun toggleRepeatMode() {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.REPEAT_MODE)) return
        repeatMode.value = when (repeatMode.value) {
            MusicRepeatMode.REPEAT -> MusicRepeatMode.REPEAT_ONE
            MusicRepeatMode.REPEAT_ONE -> MusicRepeatMode.SHUFFLE
            MusicRepeatMode.SHUFFLE -> MusicRepeatMode.REPEAT
        }
        updatePlayListForRepeatMode()
    }

    private fun updatePlayListForRepeatMode() {
        when (repeatMode.value) {
            MusicRepeatMode.REPEAT -> musicList.update { originalMusicList }
            MusicRepeatMode.SHUFFLE -> shuffleSongList()
            MusicRepeatMode.REPEAT_ONE -> Unit
        }
    }

    private fun shuffleSongList() {
        val shuffled = originalMusicList.shuffled().toMutableList()
        currentTrack.value?.let {
            shuffled.remove(it)
            shuffled.add(0, it)
        }
        musicList.update { shuffled }
    }

    fun getNextTrack(): SongResponseModel? {
        val list = musicList.value
        val index =
            list.indexOfFirst { it.id == currentTrack.value?.id }.takeIf { it >= 0 } ?: return null
        return list.getOrNull((index + 1) % list.size)
    }

    fun getPreviousTrack(): SongResponseModel? {
        val list = musicList.value
        val index =
            list.indexOfFirst { it.id == currentTrack.value?.id }.takeIf { it >= 0 } ?: return null
        return list.getOrNull(if (index == 0) list.lastIndex else index - 1)
    }
}