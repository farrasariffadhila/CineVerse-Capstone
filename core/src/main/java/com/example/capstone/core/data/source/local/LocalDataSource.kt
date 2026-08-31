package com.example.capstone.core.data.source.local

import com.example.capstone.core.data.source.local.entity.MovieEntity
import com.example.capstone.core.data.source.local.room.MovieDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val movieDao: MovieDao) {

    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>> =
        movieDao.getMoviesByCategory(category)

    fun getAllMovies(): Flow<List<MovieEntity>> =
        movieDao.getAllMovies()

    fun getFavoriteMovies(): Flow<List<MovieEntity>> =
        movieDao.getFavoriteMovies()

    fun getMovieById(id: Int): Flow<MovieEntity?> =
        movieDao.getMovieById(id)

    suspend fun getMovieByIdDirect(id: Int): MovieEntity? =
        movieDao.getMovieByIdDirect(id)

    fun searchMovies(query: String): Flow<List<MovieEntity>> =
        movieDao.searchMovies(query)

    suspend fun insertMovies(movieList: List<MovieEntity>) {
        movieDao.insertMovies(movieList)
    }

    suspend fun insertMovie(movie: MovieEntity) {
        movieDao.insertMovie(movie)
    }

    suspend fun setFavoriteMovie(movie: MovieEntity, newState: Boolean) {
        movie.isFavorite = newState
        movieDao.updateMovie(movie)
    }

    suspend fun setFavoriteMovieById(id: Int, isFavorite: Boolean) {
        movieDao.setFavoriteMovie(id, isFavorite)
    }
}
