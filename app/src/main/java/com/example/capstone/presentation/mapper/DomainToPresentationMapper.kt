package com.example.capstone.presentation.mapper

import com.example.capstone.core.domain.model.Movie
import com.example.capstone.core.utils.AppConstants
import com.example.capstone.presentation.model.MovieUiModel
import java.text.SimpleDateFormat
import java.util.Locale

object DomainToPresentationMapper {

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    fun mapDomainToUi(input: Movie): MovieUiModel {
        val formattedDate = formatReleaseDate(input.releaseDate)
        val year = extractYear(input.releaseDate)
        val poster = if (!input.posterPath.isNullOrEmpty()) {
            "${AppConstants.IMAGE_BASE_URL_POSTER}${input.posterPath}"
        } else {
            ""
        }
        val backdrop = if (!input.backdropPath.isNullOrEmpty()) {
            "${AppConstants.IMAGE_BASE_URL_BACKDROP}${input.backdropPath}"
        } else {
            poster
        }
        val ratingFormatted = String.format(Locale.US, "%.1f", input.voteAverage)
        val ratingScore = (input.voteAverage / 2.0).toFloat()
        val voteCountText = "(${input.voteCount} reviews)"

        return MovieUiModel(
            id = input.id,
            title = input.title,
            overview = input.overview,
            posterUrl = poster,
            backdropUrl = backdrop,
            formattedReleaseDate = formattedDate,
            releaseYear = year,
            formattedRating = ratingFormatted,
            ratingScore = ratingScore,
            voteCountText = voteCountText,
            category = input.category,
            isFavorite = input.isFavorite
        )
    }

    fun mapDomainListToUi(input: List<Movie>): List<MovieUiModel> =
        input.map { mapDomainToUi(it) }

    fun mapUiToDomain(input: MovieUiModel): Movie {
        val posterPath = if (input.posterUrl.startsWith(AppConstants.IMAGE_BASE_URL_POSTER)) {
            input.posterUrl.removePrefix(AppConstants.IMAGE_BASE_URL_POSTER)
        } else {
            input.posterUrl
        }
        val backdropPath = if (input.backdropUrl.startsWith(AppConstants.IMAGE_BASE_URL_BACKDROP)) {
            input.backdropUrl.removePrefix(AppConstants.IMAGE_BASE_URL_BACKDROP)
        } else {
            input.backdropUrl
        }
        val voteAverage = input.formattedRating.toDoubleOrNull() ?: 0.0

        return Movie(
            id = input.id,
            title = input.title,
            overview = input.overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = input.formattedReleaseDate,
            voteAverage = voteAverage,
            voteCount = 0,
            popularity = 0.0,
            category = input.category,
            isFavorite = input.isFavorite
        )
    }

    private fun formatReleaseDate(rawDate: String?): String {
        if (rawDate.isNullOrEmpty()) return "Unknown"
        return try {
            val date = inputDateFormat.parse(rawDate)
            if (date != null) outputDateFormat.format(date) else rawDate
        } catch (e: Exception) {
            rawDate
        }
    }

    private fun extractYear(rawDate: String?): String {
        if (rawDate.isNullOrEmpty()) return "N/A"
        return if (rawDate.length >= 4) rawDate.substring(0, 4) else rawDate
    }
}
