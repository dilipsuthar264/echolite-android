package com.echolite.app.room.repo

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.echolite.app.room.dao.RecentAlbumDao
import com.echolite.app.room.entities.AlbumEntity
import javax.inject.Inject

class RecentAlbumRepo @Inject constructor(
    private val recentAlbumDao: RecentAlbumDao
) {

    suspend fun addToRecentPlayed(album: AlbumEntity){
        recentAlbumDao.addNewRecentAlbum(album)
    }

    suspend fun removeFromRecentPlayed(albumId: String){
        recentAlbumDao.removeFromRecentPlayed(albumId)
    }

    suspend fun getRecentPlayedAlbums(): List<AlbumEntity>{
        return recentAlbumDao.getRecentPlayedAlbums()
    }
}