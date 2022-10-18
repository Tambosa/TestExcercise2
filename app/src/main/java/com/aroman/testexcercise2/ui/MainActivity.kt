package com.aroman.testexcercise2.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aroman.testexcercise2.data.retrofit.*
import com.aroman.testexcercise2.databinding.ActivityMainBinding
import com.aroman.testexcercise2.domain.PageEntity
import com.aroman.testexcercise2.utils.PaginationScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = PAGE_START
    private lateinit var adapter: PaginationAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var movieApi: MovieApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PaginationAdapter()
        layoutManager = LinearLayoutManager(this)

        binding.apply {
            movieRecyclerView.adapter = adapter
            movieRecyclerView.layoutManager = layoutManager
            movieRecyclerView.itemAnimator = DefaultItemAnimator()
            movieRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
                override fun loadMoreItems() {
                    this@MainActivity.isLoading = true
                    this@MainActivity.currentPage += 1

                    Handler().postDelayed({
                        loadNextPage()
                    }, 1000)
                }

                override val totalPageCount: Int
                    get() = TOTAL_PAGES
                override val isLastPage: Boolean
                    get() = this@MainActivity.isLastPage
                override val isLoading: Boolean
                    get() = this@MainActivity.isLoading
            })
        }

        movieApi = RetrofitClient().provideRetrofit().create(MovieApi::class.java)

        binding.mainProgressBar.visibility = View.GONE
        loadFirstPage()
    }

    private fun loadFirstPage() {
        callTopRatedMoviesApi().enqueue(object : Callback<RetrofitResponseEntity> {
            override fun onResponse(
                call: Call<RetrofitResponseEntity>,
                response: Response<RetrofitResponseEntity>
            ) {
                val result: List<PageEntity> = parseResults(response)
                binding.mainProgressBar.visibility = View.GONE
                adapter.addAll(result[0].results)
                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter()
                else isLastPage = true
            }

            override fun onFailure(call: Call<RetrofitResponseEntity>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun parseResults(response: Response<RetrofitResponseEntity>): List<PageEntity> {
        val results = response.body()
        val returnList = mutableListOf<PageEntity>()
        if (results != null) {
            returnList.add(remoteResponseToLocal(results))
        }
        return returnList
    }

    private fun loadNextPage() {
        callTopRatedMoviesApi().enqueue(object : Callback<RetrofitResponseEntity> {
            override fun onResponse(
                call: Call<RetrofitResponseEntity>,
                response: Response<RetrofitResponseEntity>
            ) {
                adapter.removeLoadingFooter()
                isLoading = false

                val result: List<PageEntity> = parseResults(response)
                adapter.addAll(result[0].results)

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter()
                else isLastPage = true
            }

            override fun onFailure(call: Call<RetrofitResponseEntity>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun callTopRatedMoviesApi(): Call<RetrofitResponseEntity> {
        val ai = packageManager.getApplicationInfo(
            this.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = ai.metaData
        val myApiKey = bundle.getString("com.aroman.testexcercise2.API_KEY")

        return movieApi.getTopRatedMovies(
            myApiKey!!,
            currentPage
        )
    }

    companion object {
        const val PAGE_START = 1
        const val TOTAL_PAGES = 5
    }
}