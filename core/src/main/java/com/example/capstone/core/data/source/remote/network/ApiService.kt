package com.example.capstone.core.data.source.remote.network

import com.example.capstone.core.data.source.remote.response.ListMovieResponse
import com.example.capstone.core.data.source.remote.response.MovieDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): ListMovieResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1
    ): ListMovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1
    ): ListMovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): ListMovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int
    ): MovieDetailResponse
}
