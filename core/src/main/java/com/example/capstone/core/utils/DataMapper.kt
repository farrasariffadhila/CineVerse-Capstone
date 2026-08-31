package com.example.capstone.core.utils

import com.example.capstone.core.data.source.local.entity.MovieEntity
import com.example.capstone.core.data.source.remote.response.MovieDetailResponse
import com.example.capstone.core.data.source.remote.response.MovieResponse
import com.example.capstone.core.domain.model.Movie

object DataMapper {

    fun mapResponsesToEntities(input: List<MovieResponse>, category: String = "popular"): List<MovieEntity> {
        val movieList = ArrayList<MovieEntity>()
        input.map {
            val movie = MovieEntity(
                id = it.id,
                title = it.title ?: "Untitled",
                overview = it.overview ?: "No overview available.",
                posterPath = it.posterPath,
                backdropPath = it.backdropPath,
                releaseDate = it.releaseDate,
                voteAverage = it.voteAverage ?: 0.0,
                voteCount = it.voteCount ?: 0,
                popularity = it.popularity ?: 0.0,
                category = category,
                isFavorite = false
            )
            movieList.add(movie)
        }
        return movieList
    }

    fun mapDetailResponseToEntity(input: MovieDetailResponse, isFavorite: Boolean = false): MovieEntity {
        return MovieEntity(
            id = input.id,
            title = input.title ?: "Untitled",
            overview = input.overview ?: "No overview available.",
            posterPath = input.posterPath,
            backdropPath = input.backdropPath,
            releaseDate = input.releaseDate,
            voteAverage = input.voteAverage ?: 0.0,
            voteCount = input.voteCount ?: 0,
            popularity = input.popularity ?: 0.0,
            category = "popular",
            isFavorite = isFavorite
        )
    }

    fun mapEntitiesToDomain(input: List<MovieEntity>): List<Movie> =
        input.map { mapEntityToDomain(it) }

    fun mapEntityToDomain(input: MovieEntity): Movie =
        Movie(
            id = input.id,
            title = input.title,
            overview = input.overview,
            posterPath = input.posterPath,
            backdropPath = input.backdropPath,
            releaseDate = input.releaseDate,
            voteAverage = input.voteAverage,
            voteCount = input.voteCount,
            popularity = input.popularity,
            category = input.category,
            isFavorite = input.isFavorite
        )

    fun mapDomainToEntity(input: Movie): MovieEntity =
        MovieEntity(
            id = input.id,
            title = input.title,
            overview = input.overview,
            posterPath = input.posterPath,
            backdropPath = input.backdropPath,
            releaseDate = input.releaseDate,
            voteAverage = input.voteAverage,
            voteCount = input.voteCount,
            popularity = input.popularity,
            category = input.category,
            isFavorite = input.isFavorite
        )

    fun mapResponsesToDomain(input: List<MovieResponse>): List<Movie> =
        input.map {
            Movie(
                id = it.id,
                title = it.title ?: "Untitled",
                overview = it.overview ?: "No overview available.",
                posterPath = it.posterPath,
                backdropPath = it.backdropPath,
                releaseDate = it.releaseDate,
                voteAverage = it.voteAverage ?: 0.0,
                voteCount = it.voteCount ?: 0,
                popularity = it.popularity ?: 0.0,
                category = "popular",
                isFavorite = false
            )
        }
}
