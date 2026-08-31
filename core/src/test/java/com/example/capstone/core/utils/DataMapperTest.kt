package com.example.capstone.core.utils

import com.example.capstone.core.data.source.local.entity.MovieEntity
import com.example.capstone.core.data.source.remote.response.MovieDetailResponse
import com.example.capstone.core.data.source.remote.response.MovieResponse
import com.example.capstone.core.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataMapperTest {

    @Test
    fun mapResponsesToEntities_returnsCorrectMapping() {
        val responses = listOf(
            MovieResponse(
                id = 101,
                title = "Inception",
                overview = "A dream within a dream.",
                posterPath = "/inception.jpg",
                backdropPath = "/inception_bg.jpg",
                releaseDate = "2010-07-16",
                voteAverage = 8.8,
                voteCount = 35000,
                popularity = 120.5,
                originalLanguage = "en"
            )
        )

        val entities = DataMapper.mapResponsesToEntities(responses, "popular")

        assertEquals(1, entities.size)
        val entity = entities[0]
        assertEquals(101, entity.id)
        assertEquals("Inception", entity.title)
        assertEquals("A dream within a dream.", entity.overview)
        assertEquals("/inception.jpg", entity.posterPath)
        assertEquals("/inception_bg.jpg", entity.backdropPath)
        assertEquals("2010-07-16", entity.releaseDate)
        assertEquals(8.8, entity.voteAverage, 0.001)
        assertEquals(35000, entity.voteCount)
        assertEquals("popular", entity.category)
        assertFalse(entity.isFavorite)
    }

    @Test
    fun mapDetailResponseToEntity_returnsCorrectMapping() {
        val detailResponse = MovieDetailResponse(
            id = 202,
            title = "Interstellar",
            overview = "Mankind was born on Earth. It was never meant to die here.",
            posterPath = "/interstellar.jpg",
            backdropPath = "/interstellar_bg.jpg",
            releaseDate = "2014-11-07",
            voteAverage = 8.7,
            voteCount = 32000,
            popularity = 140.0,
            runtime = 169,
            tagline = "Go further.",
            genres = null
        )

        val entity = DataMapper.mapDetailResponseToEntity(detailResponse, isFavorite = true)

        assertEquals(202, entity.id)
        assertEquals("Interstellar", entity.title)
        assertTrue(entity.isFavorite)
    }

    @Test
    fun mapEntitiesToDomain_and_mapDomainToEntity_areSymmetric() {
        val entity = MovieEntity(
            id = 303,
            title = "The Dark Knight",
            overview = "Why so serious?",
            posterPath = "/batman.jpg",
            backdropPath = "/batman_bg.jpg",
            releaseDate = "2008-07-18",
            voteAverage = 9.0,
            voteCount = 45000,
            popularity = 200.0,
            category = "top_rated",
            isFavorite = true
        )

        val domain = DataMapper.mapEntityToDomain(entity)
        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.isFavorite, domain.isFavorite)

        val mappedBack = DataMapper.mapDomainToEntity(domain)
        assertEquals(entity, mappedBack)
    }
}
