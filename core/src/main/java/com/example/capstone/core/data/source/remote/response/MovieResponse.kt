package com.example.capstone.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListMovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieResponse>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class MovieResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("original_language")
    val originalLanguage: String?
)

data class MovieDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("genres")
    val genres: List<GenreResponse>?
)

data class GenreResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
