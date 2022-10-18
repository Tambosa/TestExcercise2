package com.aroman.testexcercise2.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.aroman.testexcercise2.data.MovieRepositoryRetrofitImpl
import com.aroman.testexcercise2.data.retrofit.MovieApi
import com.aroman.testexcercise2.data.retrofit.RetrofitClient
import com.aroman.testexcercise2.databinding.ActivityMainBinding
import com.aroman.testexcercise2.domain.MovieRepository
import com.aroman.testexcercise2.utils.PaginationScrollListener


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = PAGE_START
    private lateinit var adapter: PaginationAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val repository: MovieRepository =
        MovieRepositoryRetrofitImpl(RetrofitClient().provideRetrofit().create(MovieApi::class.java))
    private val viewModel = MainActivityViewModel(repository)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PaginationAdapter()
        layoutManager = LinearLayoutManager(this)

        initRecyclerView()
        initViewModel()
        initData()
    }

    private fun initData() {
        adapter.addLoadingFooter()
        viewModel.getPage(getApiKey(), currentPage)
    }

    private fun initViewModel() {
        viewModel.pageList.observe(this) {
            adapter.removeLoadingFooter()
            isLoading = false
            adapter.addAll(it[0].results)
            if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter()
            else isLastPage = true
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            movieRecyclerView.adapter = adapter
            movieRecyclerView.layoutManager = layoutManager
            movieRecyclerView.itemAnimator = DefaultItemAnimator()
            movieRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
                override fun loadMoreItems() {
                    this@MainActivity.isLoading = true
                    this@MainActivity.currentPage += 1

                    Handler().postDelayed({
                        viewModel.getPage(getApiKey(), currentPage)
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
    }

    private fun getApiKey(): String {
        val ai = packageManager.getApplicationInfo(
            this.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = ai.metaData
        val myApiKey = bundle.getString("com.aroman.testexcercise2.API_KEY")
        return myApiKey ?: "Failed to get API key"
    }

    companion object {
        const val PAGE_START = 1
        const val TOTAL_PAGES = 5
    }
}