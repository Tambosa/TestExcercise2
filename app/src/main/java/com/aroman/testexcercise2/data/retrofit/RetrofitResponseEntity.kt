package com.aroman.testexcercise2.data.retrofit

import android.os.Parcelable
import com.aroman.testexcercise2.domain.MovieEntity
import com.aroman.testexcercise2.domain.PageEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RetrofitResponseEntity(
    @SerializedName("page") var page: Int,
    @SerializedName("results") var results: List<RetrofitMovieEntity>,
    @SerializedName("total_results") var totalResults: Int,
    @SerializedName("total_pages") var totalPages: Int,
) : Parcelable

@Parcelize
data class RetrofitMovieEntity(
    @SerializedName("poster_path") var posterPath: String,
    @SerializedName("adult") var isAdult: Boolean,
    @SerializedName("overview") var overview: String,
    @SerializedName("release_date") var releaseDate: String,
    @SerializedName("genre_ids") var genreIds: List<Int>,
    @SerializedName("id") var id: Int,
    @SerializedName("original_title") var originalTitle: String,
    @SerializedName("original_language") var originalLanguage: String,
    @SerializedName("title") var title: String,
    @SerializedName("backdrop_path") var backdropPath: String,
    @SerializedName("popularity") var popularity: Double,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("video") var video: Boolean,
    @SerializedName("vote_average") var voteAverage: Double
) : Parcelable

fun remoteResponseToLocal(remote: RetrofitResponseEntity) = PageEntity(
    page = remote.page,
    results = convertRemoteListToLocalList(remote.results),
    totalResults = remote.totalResults,
    totalPages = remote.totalPages
)

private fun convertRemoteListToLocalList(oldList: List<RetrofitMovieEntity>): List<MovieEntity> {
    val tempList: MutableList<MovieEntity> = mutableListOf()
    for (retrofitMovieEntity in oldList) {
        tempList.add(
            MovieEntity(
                posterPath = retrofitMovieEntity.posterPath,
                isAdult = retrofitMovieEntity.isAdult,
                overview = retrofitMovieEntity.overview,
                releaseDate = retrofitMovieEntity.releaseDate,
                genreIds = retrofitMovieEntity.genreIds,
                id = retrofitMovieEntity.id,
                originalTitle = retrofitMovieEntity.originalTitle,
                originalLanguage = retrofitMovieEntity.originalLanguage,
                title = retrofitMovieEntity.title,
                backdropPath = retrofitMovieEntity.backdropPath,
                popularity = retrofitMovieEntity.popularity,
                voteCount = retrofitMovieEntity.voteCount,
                video = retrofitMovieEntity.video,
                voteAverage = retrofitMovieEntity.voteAverage
            )
        )
    }
    return tempList
}