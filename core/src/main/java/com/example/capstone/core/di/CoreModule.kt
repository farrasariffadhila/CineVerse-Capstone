package com.example.capstone.core.di

import androidx.room.Room
import com.example.capstone.core.data.MovieRepositoryImpl
import com.example.capstone.core.data.source.local.LocalDataSource
import com.example.capstone.core.data.source.local.room.MovieDatabase
import com.example.capstone.core.data.source.remote.RemoteDataSource
import com.example.capstone.core.data.source.remote.network.ApiService
import com.example.capstone.core.domain.repository.IMovieRepository
import com.example.capstone.core.utils.AppConstants
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    factory { get<MovieDatabase>().movieDao() }
    single {
        val passphrase = SQLiteDatabase.getBytes("cineverse_secure_database_key_2026".toCharArray())
        val factory = SupportFactory(passphrase)

        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java,
            "Movie.db"
        )
            .fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}

val networkModule = module {
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val hostname = "api.themoviedb.org"
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, "sha256/QfyoR20v8hyYX7L+ikLzM/euPGSDl67gFFcor/sROMs=")
            .add(hostname, "sha256/G9LNNAql897egYsabashkzUCTEJkWBzgoEtk8X/678c=")
            .add(hostname, "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=")
            .add(hostname, "sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=")
            .build()

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithAuth = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer ${AppConstants.TMDB_BEARER_TOKEN}")
                    .addHeader("accept", "application/json")
                    .build()
                chain.proceed(requestWithAuth)
            }
            .certificatePinner(certificatePinner)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    single<IMovieRepository> { MovieRepositoryImpl(get(), get()) }
}
