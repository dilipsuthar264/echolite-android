package com.echolite.app.data.model.response

import android.provider.MediaStore.Audio.Artists
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AlbumResponseModel(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("year")
    @Expose
    var year: Int? = null,

    @SerializedName("playCount")
    @Expose
    var playCount: Any? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("explicitContent")
    @Expose
    var explicitContent: Boolean? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,

    @SerializedName("songCount")
    @Expose
    var songCount: Int? = null,

    @SerializedName("artists")
    @Expose
    var artists: SongArtistsModel? = null,

    @SerializedName("image")
    @Expose
    var image: List<ImageResponseModel>? = null,

    @SerializedName("songs")
    @Expose
    var songs: List<SongResponseModel> = emptyList(),
) :Serializable