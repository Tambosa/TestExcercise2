package com.aroman.testexcercise2.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aroman.testexcercise2.data.retrofit.RetrofitResponseEntity
import com.aroman.testexcercise2.data.retrofit.remoteResponseToLocal
import com.aroman.testexcercise2.domain.MovieRepository
import com.aroman.testexcercise2.domain.PageEntity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel(private val repo: MovieRepository) : ViewModel() {
    private val _liveData: MutableLiveData<List<PageEntity>> = MutableLiveData()
    val pageList: LiveData<List<PageEntity>> = _liveData

    fun getPage(apiKey: String, pageIndex: Int) {
        viewModelCoroutineScope.launch { susGetPage(apiKey, pageIndex) }
    }

    private suspend fun susGetPage(apiKey: String, pageIndex: Int) {
        withContext(Dispatchers.IO) {
            repo.getTopRatedMovies(apiKey, pageIndex).enqueue(object :
                Callback<RetrofitResponseEntity> {
                override fun onResponse(
                    call: Call<RetrofitResponseEntity>,
                    response: Response<RetrofitResponseEntity>
                ) {
                    val result: List<PageEntity> = parseResults(response)
                    _liveData.postValue(result)
                }

                override fun onFailure(call: Call<RetrofitResponseEntity>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun parseResults(response: Response<RetrofitResponseEntity>): List<PageEntity> {
        val results = response.body()
        val returnList = mutableListOf<PageEntity>()
        if (results != null) {
            returnList.add(remoteResponseToLocal(results))
        }
        return returnList
    }

    private val viewModelCoroutineScope = CoroutineScope(
        Dispatchers.IO
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        }
    )

    private fun handleError(error: Throwable) {
        //nothing
    }

    override fun onCleared() {
        super.onCleared()
        viewModelCoroutineScope.coroutineContext.cancel()
    }
}