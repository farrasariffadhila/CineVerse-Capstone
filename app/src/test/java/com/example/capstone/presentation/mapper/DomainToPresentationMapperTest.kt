package com.example.capstone.presentation.mapper

import com.example.capstone.core.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainToPresentationMapperTest {

    @Test
    fun mapDomainToUi_formatsDateAndRatingsCorrectly() {
        val domainMovie = Movie(
            id = 1,
            title = "Avatar: The Way of Water",
            overview = "Set more than a decade after the events of the first film.",
            posterPath = "/avatar2.jpg",
            backdropPath = "/avatar2_bg.jpg",
            releaseDate = "2022-12-14",
            voteAverage = 7.64,
            voteCount = 10500,
            popularity = 300.0,
            category = "popular",
            isFavorite = true
        )

        val uiModel = DomainToPresentationMapper.mapDomainToUi(domainMovie)

        assertEquals(1, uiModel.id)
        assertEquals("Avatar: The Way of Water", uiModel.title)
        assertEquals("2022", uiModel.releaseYear)
        assertEquals("Dec 14, 2022", uiModel.formattedReleaseDate)
        assertEquals("7.6", uiModel.formattedRating)
        assertEquals("(10500 reviews)", uiModel.voteCountText)
        assertTrue(uiModel.posterUrl.contains("/avatar2.jpg"))
        assertTrue(uiModel.backdropUrl.contains("/avatar2_bg.jpg"))
        assertTrue(uiModel.isFavorite)
    }

    @Test
    fun mapUiToDomain_preservesCoreAttributes() {
        val domainMovie = Movie(
            id = 2,
            title = "Gladiator II",
            overview = "Years after witnessing the death of Maximus.",
            posterPath = "/gladiator2.jpg",
            backdropPath = "/gladiator2_bg.jpg",
            releaseDate = "2024-11-15",
            voteAverage = 8.1,
            voteCount = 2000,
            popularity = 500.0,
            category = "popular",
            isFavorite = false
        )

        val uiModel = DomainToPresentationMapper.mapDomainToUi(domainMovie)
        val mappedBackDomain = DomainToPresentationMapper.mapUiToDomain(uiModel)

        assertEquals(domainMovie.id, mappedBackDomain.id)
        assertEquals(domainMovie.title, mappedBackDomain.title)
        assertEquals(domainMovie.isFavorite, mappedBackDomain.isFavorite)
    }
}
