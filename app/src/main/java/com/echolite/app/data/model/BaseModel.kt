package com.echolite.app.data.model

import com.echolite.app.data.ErrorResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseModel<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: Any? = null,

    val apiStatus: ApiStatus? = ApiStatus.IDLE

) : Serializable {

    companion object {

        fun <T> success(data: T?): BaseModel<T> {
            return BaseModel(
                success = true,
                apiStatus = ApiStatus.SUCCESS,
                data = data,
                message = null,
                error = null
            )
        }

        fun <T> error(errorResponse: ErrorResponse?): BaseModel<T> {
            return BaseModel(
                success = false,
                data = null,
                apiStatus = ApiStatus.ERROR,
                message = errorResponse?.message,
                error = errorResponse?.error
            )
        }

        fun <T> loading(): BaseModel<T> {
            return BaseModel(
                success = false,
                data = null,
                apiStatus = ApiStatus.LOADING,
                message = null
            )
        }

        fun <T> idle(): BaseModel<T> {
            return BaseModel(
                success = false,
                data = null,
                apiStatus = ApiStatus.IDLE,
                message = null
            )
        }

        fun <T> loading(data: T?): BaseModel<T> {
            return BaseModel(
                success = false,
                data = data,
                apiStatus = ApiStatus.LOADING,
                message = null
            )
        }
    }

    fun isLoading(): Boolean = apiStatus == ApiStatus.LOADING
    fun isError(): Boolean = apiStatus == ApiStatus.ERROR
    fun isSuccess(): Boolean = apiStatus == ApiStatus.SUCCESS
    fun isIdle(): Boolean = apiStatus == ApiStatus.IDLE
}

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING,
    IDLE
}