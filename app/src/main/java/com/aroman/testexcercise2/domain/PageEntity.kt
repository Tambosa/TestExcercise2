package com.aroman.testexcercise2.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PageEntity(
    var page: Int,
    var results: List<MovieEntity>,
    var totalResults: Int,
    var totalPages: Int,
) : Parcelable

@Parcelize
data class MovieEntity(
    var posterPath: String = "",
    var isAdult: Boolean = false,
    var overview: String = "",
    var releaseDate: String = "",
    var genreIds: List<Int> = listOf(),
    var id: Int = 0,
    var originalTitle: String = "",
    var originalLanguage: String = "",
    var title: String = "",
    var backdropPath: String = "",
    var popularity: Double = 0.0,
    var voteCount: Int = 0,
    var video: Boolean = false,
    var voteAverage: Double = 0.0
) : Parcelable