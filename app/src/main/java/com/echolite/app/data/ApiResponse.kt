package com.echolite.app.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

sealed class ApiResponse<out T>(
    val isSuccessful: Boolean,
    val response: T? = null,
    val errorResponse: ErrorResponse? = null
) {
    data class Success<out T>(val responseData: T?) : ApiResponse<T>(true, responseData)
    data class Failure<out T>(val errorData: ErrorResponse?) :
        ApiResponse<T>(false, null, errorData)
}


data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: Any? = null,
) : Serializable