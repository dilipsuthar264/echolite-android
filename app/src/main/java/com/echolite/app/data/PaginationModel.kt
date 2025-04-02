package com.echolite.app.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaginationModel<T>(

    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("start")
    val start: Int? = null,

    @SerializedName("results")
    val results: List<T> = emptyList(),

    @SerializedName("songs")
    val songs: List<T> = emptyList(),

    @SerializedName("albums")
    val albums: List<T> = emptyList()

) : Serializable