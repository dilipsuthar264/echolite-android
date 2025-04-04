package com.echolite.app.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.echolite.app.room.entities.FavoriteSongEntity

@Dao
interface FavoriteSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorite(song: FavoriteSongEntity)

    @Query("DELETE FROM favorite_song_table WHERE song_id = :songId")
    suspend fun removeFromFavorites(songId: String)

    @Query("SELECT * FROM favorite_song_table WHERE song_id= :songId")
    suspend fun getFavoriteSongById(songId: String): FavoriteSongEntity?

    @Query("SELECT * FROM favorite_song_table ORDER BY id DESC")
    suspend fun getAllFavorites(): List<FavoriteSongEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_song_table WHERE song_id = :songId LIMIT 1)")
    suspend fun isSongFavorite(songId: String): Boolean

    @Transaction
    suspend fun toggleFavorite(song: FavoriteSongEntity) {
        if (isSongFavorite(song.songId ?: "")) {
            removeFromFavorites(song.songId ?: "")
        } else {
            addToFavorite(song)
        }
    }
}