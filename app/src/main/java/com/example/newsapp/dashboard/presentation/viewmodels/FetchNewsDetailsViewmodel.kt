package com.example.newsapp.dashboard.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.dashboard.data.model.GetNewsDataParams
import com.example.newsapp.dashboard.data.repository.GetNewsDetailsRepository
import com.example.newsapp.dashboard.domain.mapper.GetNewsDataMapper
import com.example.newsapp.dashboard.domain.model.ArticleDomainData
import com.example.newsapp.dashboard.domain.usecase.GetNewsDataUseCase
import com.example.newsapp.network.RetrofitClient
import com.example.newsapp.utils.PreferenceManager
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.VariableBag
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FetchNewsDetailsViewmodel(application: Application) : AndroidViewModel(application) {

    private val TAG = "FetchNewsVM"
    private val _newsResponse = MutableLiveData<Resource<List<ArticleDomainData>>>()
    val newsResponse: LiveData<Resource<List<ArticleDomainData>>> = _newsResponse

    private val allArticles = mutableListOf<ArticleDomainData>()
    private var currentPage = 1
    private var isLastPage = false
    private var isFetching = false

    private val getNewsDataUseCase: GetNewsDataUseCase = GetNewsDataUseCase(
        repository = GetNewsDetailsRepository(
            apiServices = RetrofitClient.apiService,
            mapper = GetNewsDataMapper(),
            preferenceManager = PreferenceManager(application)
        )
    )

    fun fetchNews(keyWord: String = "tesla", isInitialLoad: Boolean = false) {
        if (isFetching || (isLastPage && !isInitialLoad)) {
            return
        }

        if (isInitialLoad) {
            currentPage = 1
            allArticles.clear()
            isLastPage = false
        }

        isFetching = true
        _newsResponse.postValue(Resource.Loading())

        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                val fromDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                val params = GetNewsDataParams(
                    query = keyWord,
                    from = fromDate,
                    sortBy = "publishedAt",
                    apiKey = VariableBag.API_KEY,
                    page = currentPage
                )

                val domainData = getNewsDataUseCase.getNewsData(params)

                if (domainData != null && domainData.articles.isNotEmpty()) {
                    allArticles.addAll(domainData.articles)
                    _newsResponse.postValue(Resource.Success(allArticles.toList()))
                    currentPage++
                } else {
                    if (isInitialLoad) {
                        _newsResponse.postValue(Resource.Error("No news found for '$keyWord'"))
                    } else {
                        _newsResponse.postValue(Resource.Success(allArticles.toList()))
                    }
                    isLastPage = true
                }
            } catch (e: Exception) {
                _newsResponse.postValue(Resource.Error(e.message ?: "An unknown error occurred"))
            } finally {
                isFetching = false
            }
        }
    }
}
