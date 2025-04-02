package com.echolite.app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ImageResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "artist_table")
data class ArtistEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "artist_id")
    val artistId: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "image")
    val image: List<String> = emptyList(),
)