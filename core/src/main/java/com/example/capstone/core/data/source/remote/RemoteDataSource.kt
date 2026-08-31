package com.example.capstone.core.data.source.remote

import com.example.capstone.core.data.source.remote.network.ApiResponse
import com.example.capstone.core.data.source.remote.network.ApiService
import com.example.capstone.core.data.source.remote.response.MovieDetailResponse
import com.example.capstone.core.data.source.remote.response.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {

    fun getPopularMovies(): Flow<ApiResponse<List<MovieResponse>>> = flow {
        try {
            val response = apiService.getPopularMovies()
            val dataArray = response.results
            if (dataArray.isNotEmpty()) {
                emit(ApiResponse.Success(dataArray))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Failed to fetch popular movies"))
        }
    }.flowOn(Dispatchers.IO)

    fun getNowPlayingMovies(): Flow<ApiResponse<List<MovieResponse>>> = flow {
        try {
            val response = apiService.getNowPlayingMovies()
            val dataArray = response.results
            if (dataArray.isNotEmpty()) {
                emit(ApiResponse.Success(dataArray))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Failed to fetch now playing movies"))
        }
    }.flowOn(Dispatchers.IO)

    fun getTopRatedMovies(): Flow<ApiResponse<List<MovieResponse>>> = flow {
        try {
            val response = apiService.getTopRatedMovies()
            val dataArray = response.results
            if (dataArray.isNotEmpty()) {
                emit(ApiResponse.Success(dataArray))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Failed to fetch top rated movies"))
        }
    }.flowOn(Dispatchers.IO)

    fun searchMovies(query: String): Flow<ApiResponse<List<MovieResponse>>> = flow {
        try {
            val response = apiService.searchMovies(query)
            val dataArray = response.results
            if (dataArray.isNotEmpty()) {
                emit(ApiResponse.Success(dataArray))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Failed to search movies"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMovieDetail(movieId: Int): Flow<ApiResponse<MovieDetailResponse>> = flow {
        try {
            val response = apiService.getMovieDetail(movieId)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.localizedMessage ?: "Failed to fetch movie detail"))
        }
    }.flowOn(Dispatchers.IO)
}
