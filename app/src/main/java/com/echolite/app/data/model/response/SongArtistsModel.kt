package com.echolite.app.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SongArtistsModel(
    @SerializedName("primary")
    @Expose
    var primary: List<ArtistResponseModel> = emptyList(),

    @SerializedName("featured")
    @Expose
    var featured: List<Any>? = null,

    @SerializedName("all")
    @Expose
    var all: List<ArtistResponseModel> = emptyList(),
) : Serializable