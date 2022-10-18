package com.aroman.testexcercise2.ui

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.aroman.testexcercise2.R
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
    private val adapter = PaginationAdapter(
        { position ->
            onOverviewClick(position)
        },
        { position ->
            onPosterClick(position)
        })
    private lateinit var layoutManager: LinearLayoutManager
    private val repository: MovieRepository =
        MovieRepositoryRetrofitImpl(RetrofitClient().provideRetrofit().create(MovieApi::class.java))
    private val viewModel = MainActivityViewModel(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this)

        initRecyclerView()
        initViewModel()
        initData()
    }

    private fun onOverviewClick(position: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Movie Description")
            setMessage(adapter.getData()[position].overview)
            show()
        }
    }

    private fun onPosterClick(position: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.alert_image, null)
        val image = view.findViewById<ImageView>(R.id.dialog_imageview)
        image.load("https://image.tmdb.org/t/p/w780" + adapter.getData()[position].posterPath)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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