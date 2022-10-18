package com.aroman.testexcercise2.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aroman.testexcercise2.R
import com.aroman.testexcercise2.domain.MovieEntity


class PaginationAdapter(
    private val onOverviewClick: (position: Int) -> Unit,
    private val onPosterClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val ITEM = 0
        const val LOADING = 1
        const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w185"
    }

    private var movieList = arrayListOf<MovieEntity>()
    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM -> MovieVH(inflater.inflate(R.layout.item_movie_recycler, parent, false))
            else -> LoadingVH(inflater.inflate(R.layout.item_progress, parent, false))
        }
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

                movieVH.mMovieDesc.setOnClickListener { onOverviewClick(position) }
                movieVH.mPosterImg.setOnClickListener { onPosterClick(position) }
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

    fun getData() = movieList

    inner class MovieVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mMovieTitle: TextView
        val mMovieDesc: TextView
        val mYear: TextView
        val mPosterImg: ImageView

        init {
            mMovieTitle = itemView.findViewById<View>(R.id.movie_title) as TextView
            mMovieDesc = itemView.findViewById<View>(R.id.movie_desc) as TextView
            mYear = itemView.findViewById<View>(R.id.movie_year) as TextView
            mPosterImg = itemView.findViewById<View>(R.id.movie_poster) as ImageView
        }
    }

    inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}