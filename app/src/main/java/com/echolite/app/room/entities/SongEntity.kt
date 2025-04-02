package com.echolite.app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.DownloadUrlModel
import com.echolite.app.data.model.response.ImageResponseModel
import com.echolite.app.data.model.response.SongArtistsModel

@Entity(
    tableName = "song_table"
)
data class SongEntity(
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