package com.aroman.testexcercise2.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aroman.testexcercise2.R
import com.aroman.testexcercise2.domain.MovieEntity


class PaginationAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val ITEM = 0
        const val LOADING = 1
        const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w150"
    }

    private var movieList = arrayListOf<MovieEntity>()
    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM -> getViewHolder(parent, LayoutInflater.from(parent.context))
            else -> LoadingVH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
            )
        }
    }

    private fun getViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): RecyclerView.ViewHolder {
        return MovieVH(inflater.inflate(R.layout.item_movie_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = movieList[position]
        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH
                movieVH.mMovieTitle.text = movie.title
                movieVH.mYear.text = (movie.releaseDate.substring(0, 4)
                        + " | "
                        + movie.originalLanguage.uppercase())
                movieVH.mMovieDesc.text = movie.overview
                Log.d("@@@", "onBindViewHolder: ${BASE_IMAGE_URL + movie.posterPath}")
                movieVH.mPosterImg.load(BASE_IMAGE_URL + movie.posterPath)
                movieVH.mProgress.visibility = View.GONE
            }
            else -> { //nothing
            }
        }
    }

    override fun getItemCount() = movieList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == movieList.size - 1 && isLoading) LOADING else ITEM
    }

    private fun add(movie: MovieEntity) {
        movieList.add(movie)
        notifyItemInserted(movieList.size - 1)
    }

    fun addAll(list: List<MovieEntity>) {
        for (movie in list) {
            add(movie)
        }
    }

    fun addLoadingFooter() {
        isLoading = true
        add(MovieEntity())
    }

    fun removeLoadingFooter() {
        isLoading = false
        movieList.removeAt(movieList.size - 1)
        notifyItemRemoved(movieList.size - 1)
    }


    inner class MovieVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mMovieTitle: TextView
        val mMovieDesc: TextView
        val mYear: TextView
        val mPosterImg: ImageView
        val mProgress: ProgressBar

        init {
            mMovieTitle = itemView.findViewById<View>(R.id.movie_title) as TextView
            mMovieDesc = itemView.findViewById<View>(R.id.movie_desc) as TextView
            mYear = itemView.findViewById<View>(R.id.movie_year) as TextView
            mPosterImg = itemView.findViewById<View>(R.id.movie_poster) as ImageView
            mProgress = itemView.findViewById<View>(R.id.movie_progress) as ProgressBar
            mProgress.visibility = View.VISIBLE
        }
    }

    inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}