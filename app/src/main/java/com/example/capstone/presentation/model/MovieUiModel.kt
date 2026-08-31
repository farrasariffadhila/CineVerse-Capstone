package com.example.capstone.presentation.model

import java.io.Serializable

data class MovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val formattedReleaseDate: String,
    val releaseYear: String,
    val formattedRating: String,
    val ratingScore: Float,
    val voteCountText: String,
    val category: String,
    val isFavorite: Boolean
) : Serializable
