package com.echolite.app.room.repo

import com.echolite.app.room.dao.RecentSongDao
import com.echolite.app.room.entities.SongEntity
import javax.inject.Inject

class RecentSongRepo @Inject constructor(
    private val recentSongDao: RecentSongDao
) {
    suspend fun addToRecentPlayed(song: SongEntity) {
        recentSongDao.addNewRecentPlayed(song)
    }
    suspend fun removeFromRecentPlayed(songId: String) {
        recentSongDao.removeFromRecentPlayed(songId)
    }
    suspend fun getRecentPlayedSongs(): List<SongEntity> {
        return recentSongDao.getRecentPlayedSongs()
    }
}