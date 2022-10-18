package com.aroman.testexcercise2.domain

import com.aroman.testexcercise2.data.retrofit.RetrofitResponseEntity
import retrofit2.Call

interface MovieRepository {
    suspend fun getPopularMovies(apiKey: String, pageIndex: Int): Call<RetrofitResponseEntity>
    suspend fun getTopRatedMovies(apiKey: String, pageIndex: Int): Call<RetrofitResponseEntity>
}