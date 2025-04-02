package com.echolite.app.data.model.response

import android.media.Image
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ArtistResponseModel(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("followerCount")
    @Expose
    var followerCount: Int? = null,

    @SerializedName("fanCount")
    @Expose
    var fanCount: String? = null,

    @SerializedName("isVerified")
    @Expose
    var isVerified: Boolean? = null,

    @SerializedName("dominantLanguage")
    @Expose
    var dominantLanguage: String? = null,

    @SerializedName("dominantType")
    @Expose
    var dominantType: String? = null,

    @SerializedName("bio")
    @Expose
    var bio: List<Any>? = null,

    @SerializedName("dob")
    @Expose
    var dob: Any? = null,

    @SerializedName("fb")
    @Expose
    var fb: String? = null,

    @SerializedName("twitter")
    @Expose
    var twitter: String? = null,

    @SerializedName("wiki")
    @Expose
    var wiki: String? = null,

    @SerializedName("availableLanguages")
    @Expose
    var availableLanguages: List<String>? = null,

    @SerializedName("isRadioPresent")
    @Expose
    var isRadioPresent: Boolean? = null,

    @SerializedName("image")
    @Expose
    var image: List<ImageResponseModel>? = null,

    @SerializedName("topSongs")
    @Expose
    var topSongs: List<SongResponseModel> = emptyList(),

    @SerializedName("topAlbums")
    @Expose
    var topAlbums: List<AlbumResponseModel> = emptyList(),

    @SerializedName("singles")
    @Expose
    var singles: List<SongResponseModel>? = null,

    @SerializedName("similarArtists")
    @Expose
    var similarArtists: List<Any>? = null,
) : Serializable