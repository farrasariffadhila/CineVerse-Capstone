package com.example.capstone.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.capstone.core.data.source.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY popularity DESC")
    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE is_favorite = 1 ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun getMovieById(id: Int): Flow<MovieEntity?>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieByIdDirect(id: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY popularity DESC")
    fun searchMovies(query: String): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(movies: List<MovieEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("UPDATE movies SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun setFavoriteMovie(id: Int, isFavorite: Boolean)
}
