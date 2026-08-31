package com.example.capstone.core.domain.model

import java.io.Serializable

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val category: String = "popular",
    val isFavorite: Boolean = false
) : Serializable
