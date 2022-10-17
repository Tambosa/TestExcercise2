package com.aroman.testexcercise2.data

import com.aroman.testexcercise2.data.retrofit.MovieApi
import com.aroman.testexcercise2.data.retrofit.RetrofitResponseEntity
import com.aroman.testexcercise2.domain.MovieRepository
import retrofit2.Call

class MovieRepositoryRetrofitImpl(private val movieApi: MovieApi) : MovieRepository {
    override fun getPopularMovies(apiKey: String, pageIndex: Int): Call<RetrofitResponseEntity> {
        return movieApi.getPopularMovies(apiKey, pageIndex)
    }

    override fun getTopRatedMovies(apiKey: String, pageIndex: Int): Call<RetrofitResponseEntity> {
        return movieApi.getTopRatedMovies(apiKey, pageIndex)
    }
}