package com.example.capstone.core.data

import com.example.capstone.core.data.source.local.LocalDataSource
import com.example.capstone.core.data.source.remote.RemoteDataSource
import com.example.capstone.core.data.source.remote.network.ApiResponse
import com.example.capstone.core.data.source.remote.response.MovieDetailResponse
import com.example.capstone.core.data.source.remote.response.MovieResponse
import com.example.capstone.core.domain.model.Movie
import com.example.capstone.core.domain.repository.IMovieRepository
import com.example.capstone.core.utils.DataMapper
import com.example.capstone.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IMovieRepository {

    override fun getPopularMovies(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>() {
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getMoviesByCategory("popular").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data.isNullOrEmpty()
            }

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> {
                return remoteDataSource.getPopularMovies()
            }

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data, "popular")
                localDataSource.insertMovies(movieList)
            }
        }.asFlow()

    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>() {
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getMoviesByCategory("now_playing").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data.isNullOrEmpty()
            }

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> {
                return remoteDataSource.getNowPlayingMovies()
            }

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data, "now_playing")
                localDataSource.insertMovies(movieList)
            }
        }.asFlow()

    override fun getTopRatedMovies(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>() {
            override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getMoviesByCategory("top_rated").map {
                    DataMapper.mapEntitiesToDomain(it)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data.isNullOrEmpty()
            }

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> {
                return remoteDataSource.getTopRatedMovies()
            }

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val movieList = DataMapper.mapResponsesToEntities(data, "top_rated")
                localDataSource.insertMovies(movieList)
            }
        }.asFlow()

    override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> =
        object : NetworkBoundResource<Movie, MovieDetailResponse>() {
            override fun loadFromDB(): Flow<Movie> {
                return localDataSource.getMovieById(movieId).map {
                    it?.let { DataMapper.mapEntityToDomain(it) } ?: Movie(
                        id = movieId,
                        title = "",
                        overview = "",
                        posterPath = null,
                        backdropPath = null,
                        releaseDate = null,
                        voteAverage = 0.0,
                        voteCount = 0,
                        popularity = 0.0
                    )
                }
            }

            override fun shouldFetch(data: Movie?): Boolean {
                return data == null || data.title.isEmpty() || data.overview.isEmpty()
            }

            override suspend fun createCall(): Flow<ApiResponse<MovieDetailResponse>> {
                return remoteDataSource.getMovieDetail(movieId)
            }

            override suspend fun saveCallResult(data: MovieDetailResponse) {
                val existing = localDataSource.getMovieByIdDirect(data.id)
                val isFavorite = existing?.isFavorite ?: false
                val entity = DataMapper.mapDetailResponseToEntity(data, isFavorite)
                localDataSource.insertMovie(entity)
            }
        }.asFlow()

    override fun searchMovies(query: String): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            remoteDataSource.searchMovies(query).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        val domainList = DataMapper.mapResponsesToDomain(response.data)
                        emit(Resource.Success(domainList))
                    }
                    is ApiResponse.Empty -> {
                        emit(Resource.Success(emptyList()))
                    }
                    is ApiResponse.Error -> {
                        emit(Resource.Error(response.errorMessage))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to search movies"))
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        localDataSource.getFavoriteMovies().map {
            DataMapper.mapEntitiesToDomain(it)
        }

    override fun isFavorite(movieId: Int): Flow<Boolean> =
        localDataSource.getMovieById(movieId).map {
            it?.isFavorite ?: false
        }

    override suspend fun setFavoriteMovie(movie: Movie, state: Boolean) {
        val existing = localDataSource.getMovieByIdDirect(movie.id)
        if (existing == null) {
            val entity = DataMapper.mapDomainToEntity(movie)
            entity.isFavorite = state
            localDataSource.insertMovie(entity)
        } else {
            localDataSource.setFavoriteMovieById(movie.id, state)
        }
    }
}
