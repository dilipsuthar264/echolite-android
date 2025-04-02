package com.echolite.app.data.pagginSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.echolite.app.data.BaseRepository
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.data.services.ArtistApi
import java.io.IOException

class ArtistAlbumPagingSource(
    private val artistApi: ArtistApi,
    private val artistId: String,
    private val queryModel: QueryModel,
) : PagingSource<Int, AlbumResponseModel>(), BaseRepository {

    override fun getRefreshKey(state: PagingState<Int, AlbumResponseModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumResponseModel> {
        val page = params.key ?: queryModel.page
        val limit = queryModel.limit

        if (artistId.isEmpty()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }
        return try {
            val response = handleData(handleResponse {
                artistApi.getArtistAlbums(
                    artistId = artistId,
                    page = page,
                    limit = limit,
                    sortBy = queryModel.sortBy,
                    sortOrder = queryModel.sortOrder
                )
            })
            val isListEnd = response.data?.albums?.isEmpty()
            LoadResult.Page(
                data = response.data?.albums ?: emptyList(),
                prevKey = if (page == 0) null else page.minus(1),
                nextKey = if (isListEnd == true || isListEnd == null) null else page.plus(1)
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}