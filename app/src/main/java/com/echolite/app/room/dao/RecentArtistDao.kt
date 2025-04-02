package com.echolite.app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.echolite.app.room.entities.ArtistEntity

@Dao
interface RecentArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToRecentPlayed(artist: ArtistEntity)

    @Query("DELETE FROM artist_table WHERE artist_id = :artistId")
    suspend fun removeFromRecentPlayed(artistId: String)


    @Query("SELECT * FROM artist_table ORDER BY id DESC LIMIT 10")
    suspend fun getRecentPlayedArtists(): List<ArtistEntity>

    @Transaction
    suspend fun addNewRecentPlayed(artist: ArtistEntity) {
        // First, remove the artist if it's already in the recent played list
        removeFromRecentPlayed(artist.artistId ?: "")

        // Then, add the new artist to the recent played list
        addToRecentPlayed(artist)

        // If there are more than 10 artists, remove the oldest one
        val recentArtists = getRecentPlayedArtists()
        if (recentArtists.size > 10) {
            removeFromRecentPlayed(recentArtists.last().artistId ?: "")
        }
    }

}