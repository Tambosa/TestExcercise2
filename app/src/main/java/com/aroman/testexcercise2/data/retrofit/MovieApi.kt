package com.aroman.testexcercise2.data.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") pageIndex: Int
    ): Call<RetrofitResponseEntity>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") pageIndex: Int
    ): Call<RetrofitResponseEntity>
}