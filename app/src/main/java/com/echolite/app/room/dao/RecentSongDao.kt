package com.echolite.app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.echolite.app.room.entities.SongEntity

@Dao
interface RecentSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToRecentPlayed(song: SongEntity)

    @Query("DELETE FROM song_table WHERE song_id = :songId")
    suspend fun removeFromRecentPlayed(songId: String)

    @Query("SELECT * FROM song_table ORDER BY id DESC Limit 15")
    suspend fun getRecentPlayedSongs(): List<SongEntity>

    @Transaction
    suspend fun addNewRecentPlayed(song: SongEntity) {
        // First, remove the song if it's already in the recent played list
        removeFromRecentPlayed(song.songId ?: "")

        // Then, add the new song to the recent played list
        addToRecentPlayed(song)

        // If there are more than 15 songs, remove the oldest one
        val recentSongs = getRecentPlayedSongs()
        if (recentSongs.size > 15) {
            removeFromRecentPlayed(recentSongs.last().songId ?: "")
        }
    }
}