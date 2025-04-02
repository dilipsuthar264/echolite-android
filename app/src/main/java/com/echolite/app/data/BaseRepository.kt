package com.echolite.app.data

import com.echolite.app.data.model.BaseModel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Response

interface BaseRepository {


    suspend fun <T> handleResponse(call: suspend () -> Response<T>): ApiResponse<T> {
        try {
            val response = call.invoke()

            return when {
                response.isSuccessful -> {
                    ApiResponse.Success(response.body())
                }

                else -> {
                    response.errorBody()?.let {
                        val jsonObject = JSONObject(it.toString()).toString()
                        val error = Gson().fromJson(jsonObject, ErrorResponse::class.java)
                        ApiResponse.Failure(errorData = error)
                    } ?: run {
                        ApiResponse.Failure(
                            errorData = ErrorResponse(
                                success = false, message = "Something went wrong!"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return ApiResponse.Failure(
                errorData = ErrorResponse(
                    success = false, message = e.message
                )
            )
        }
    }


    suspend fun <T> handleData(result: ApiResponse<BaseModel<T>>): BaseModel<T> {
        return if (result.isSuccessful) {
            result.response?.let {
                BaseModel.success(it.data)
            } ?: BaseModel.error(result.errorResponse)
        } else {
            BaseModel.error(result.errorResponse)
        }
    }
}