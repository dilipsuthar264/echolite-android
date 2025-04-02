package com.echolite.app.room.repo

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.echolite.app.room.dao.RecentArtistDao
import com.echolite.app.room.entities.ArtistEntity
import javax.inject.Inject

class RecentArtistRepo @Inject constructor(
    private val recentArtistDao: RecentArtistDao
){

    suspend fun addToRecentPlayed(artist: ArtistEntity){
        recentArtistDao.addNewRecentPlayed(artist)
    }

    suspend fun removeFromRecentPlayed(artistId: String){
        recentArtistDao.removeFromRecentPlayed(artistId)
    }

    suspend fun getRecentPlayedArtists(): List<ArtistEntity>{
        return recentArtistDao.getRecentPlayedArtists()
    }

}