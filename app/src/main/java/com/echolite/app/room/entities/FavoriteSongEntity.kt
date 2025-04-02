package com.echolite.app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_song_table")
data class FavoriteSongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "song_id")
    var songId: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "duration")
    var duration: Int? = null,

    @ColumnInfo(name = "album")
    var albumResponseModel: String? = null,

    @ColumnInfo(name = "artists")
    var songArtistsModel: String? = null,

    @ColumnInfo(name = "image")
    var image: List<String> = emptyList(),

    @ColumnInfo(name = "downloadUrl")
    var downloadUrl: List<String> = emptyList(),
)
