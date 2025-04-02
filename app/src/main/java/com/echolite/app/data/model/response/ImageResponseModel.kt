package com.echolite.app.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageResponseModel(
    @SerializedName("quality")
    @Expose
    var quality: String? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,
) : Serializable