package com.echolite.app.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SongResponseModel(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("year")
    @Expose
    var year: String? = null,

    @SerializedName("releaseDate")
    @Expose
    var releaseDate: Any? = null,

    @SerializedName("duration")
    @Expose
    var duration: Int? = null,

    @SerializedName("label")
    @Expose
    var label: String? = null,

    @SerializedName("explicitContent")
    @Expose
    var explicitContent: Boolean? = null,

    @SerializedName("playCount")
    @Expose
    var playCount: Int? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("hasLyrics")
    @Expose
    var hasLyrics: Boolean? = null,

    @SerializedName("lyricsId")
    @Expose
    var lyricsId: Any? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,

    @SerializedName("copyright")
    @Expose
    var copyright: String? = null,

    @SerializedName("album")
    @Expose
    var album: AlbumResponseModel? = null,

    @SerializedName("artists")
    @Expose
    var artists: SongArtistsModel? = null,

    @SerializedName("image")
    @Expose
    var image: List<ImageResponseModel>? = null,

    @SerializedName("downloadUrl")
    @Expose
    var downloadUrl: List<DownloadUrlModel>? = null,

    val isPlaying: Boolean = false
) : Serializable