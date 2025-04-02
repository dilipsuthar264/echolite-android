package com.echolite.app.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.echolite.app.room.entities.AlbumEntity

@Dao
interface RecentAlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToRecentPlayed(album: AlbumEntity)

    @Query("DELETE FROM album_table WHERE album_id = :albumId")
    suspend fun removeFromRecentPlayed(albumId: String)

    @Query("SELECT * FROM album_table ORDER BY id DESC LIMIT 10")
    suspend fun getRecentPlayedAlbums(): List<AlbumEntity>

    @Transaction
    suspend fun addNewRecentAlbum(album: AlbumEntity) {
        // First, remove the album if it's already in the recent played list
        removeFromRecentPlayed(album.albumId ?: "")

        // Then, add the new album to the recent played list
        addToRecentPlayed(album)

        // If there are more than 10 albums, remove the oldest one
        val recentAlbums = getRecentPlayedAlbums()
        if (recentAlbums.size > 10) {
            removeFromRecentPlayed(recentAlbums.last().albumId ?: "")
        }
    }

}