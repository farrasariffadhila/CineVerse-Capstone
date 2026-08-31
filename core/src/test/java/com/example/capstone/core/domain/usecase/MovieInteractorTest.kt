package com.example.capstone.core.domain.usecase

import com.example.capstone.core.domain.model.Movie
import com.example.capstone.core.domain.repository.IMovieRepository
import com.example.capstone.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieInteractorTest {

    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var movieUseCase: MovieUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeMovieRepository()
        movieUseCase = MovieInteractor(fakeRepository)
    }

    @Test
    fun getPopularMovies_returnsSuccessFromRepository() = runTest {
        val result = movieUseCase.getPopularMovies().first()
        assertTrue(result is Resource.Success)
        assertEquals(1, result.data?.size)
        assertEquals("Dune: Part Two", result.data?.get(0)?.title)
    }

    @Test
    fun getFavoriteMovies_returnsFavoritesList() = runTest {
        val result = movieUseCase.getFavoriteMovies().first()
        assertEquals(1, result.size)
        assertTrue(result[0].isFavorite)
    }

    @Test
    fun setFavoriteMovie_updatesStateSuccessfully() = runTest {
        val dummyMovie = Movie(
            id = 555,
            title = "Oppenheimer",
            overview = "Now I am become Death.",
            posterPath = "/oppenheimer.jpg",
            backdropPath = "/oppenheimer_bg.jpg",
            releaseDate = "2023-07-21",
            voteAverage = 8.9,
            voteCount = 20000,
            popularity = 180.0,
            isFavorite = false
        )

        movieUseCase.setFavoriteMovie(dummyMovie, true)
        val isFav = movieUseCase.isFavorite(555).first()
        assertTrue(isFav)
    }

    private class FakeMovieRepository : IMovieRepository {
        private val sampleMovies = listOf(
            Movie(
                id = 693134,
                title = "Dune: Part Two",
                overview = "Follow the mythic journey of Paul Atreides.",
                posterPath = "/dune2.jpg",
                backdropPath = "/dune2_bg.jpg",
                releaseDate = "2024-02-27",
                voteAverage = 8.3,
                voteCount = 4500,
                popularity = 450.0,
                category = "popular",
                isFavorite = false
            )
        )

        private val favorites = mutableMapOf<Int, Boolean>(
            999 to true
        )

        override fun getPopularMovies(): Flow<Resource<List<Movie>>> =
            flowOf(Resource.Success(sampleMovies))

        override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> =
            flowOf(Resource.Success(sampleMovies))

        override fun getTopRatedMovies(): Flow<Resource<List<Movie>>> =
            flowOf(Resource.Success(sampleMovies))

        override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> =
            flowOf(Resource.Success(sampleMovies[0]))

        override fun searchMovies(query: String): Flow<Resource<List<Movie>>> =
            flowOf(Resource.Success(sampleMovies))

        override fun getFavoriteMovies(): Flow<List<Movie>> =
            flowOf(listOf(sampleMovies[0].copy(id = 999, isFavorite = true)))

        override fun isFavorite(movieId: Int): Flow<Boolean> =
            flowOf(favorites[movieId] ?: false)

        override suspend fun setFavoriteMovie(movie: Movie, state: Boolean) {
            favorites[movie.id] = state
        }
    }
}
