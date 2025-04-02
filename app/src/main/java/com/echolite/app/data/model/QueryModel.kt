package com.echolite.app.data.model

import java.io.Serializable

data class QueryModel(
    val query: String? = null,
    val page: Int = 1,
    val limit: Int = 10,
    val songCount: Int? = 5,
    val albumCount: Int? = 5,
    val sortBy: String? = null,
    val sortOrder: String? = null,
) : Serializable