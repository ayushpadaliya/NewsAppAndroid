package com.example.newsapp.dashboard.presentation.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.dashboard.domain.model.ArticleDomainData
import com.example.newsapp.dashboard.presentation.adapter.ViewDailyNewsAdapter
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.dashboard.presentation.viewmodels.FetchNewsDetailsViewmodel
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.*

class NewsFeedActivity : AppCompatActivity(), ViewDailyNewsAdapter.OnItemClickListener {

    private val TAG = "NewsFeedActivity"
    private lateinit var b: ActivityMainBinding
    private val viewModel: FetchNewsDetailsViewmodel by viewModels()
    private lateinit var newsAdapter: ViewDailyNewsAdapter

    private var isLoading = false
    private var isScrolling = false
    private var searchJob: Job? = null
    private var currentQuery = "tesla"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSwipeRefresh()
        setupSearch()
        observeViewModel()

        viewModel.fetchNews(currentQuery, isInitialLoad = true)
    }

    private fun setupRecyclerView() {
        newsAdapter = ViewDailyNewsAdapter(listener = this)
        b.rvNews.apply {
            layoutManager = LinearLayoutManager(this@NewsFeedActivity)
            adapter = newsAdapter
            addOnScrollListener(this@NewsFeedActivity.scrollListener)
        }
    }

    private fun setupSwipeRefresh() {
        b.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchNews(currentQuery, isInitialLoad = true)
        }
    }

    private fun setupSearch() {
        b.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    b.ivClear.visibility = View.GONE
                } else {
                    b.ivClear.visibility = View.VISIBLE
                }
                
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500L)
                    performSearch()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        b.ivClear.setOnClickListener {
            b.etSearch.text.clear()
            // When cleared, explicitly reset currentQuery and fetch
            currentQuery = "tesla"
            newsAdapter.updateData(emptyList())
            viewModel.fetchNews(currentQuery, isInitialLoad = true)
        }
    }

    private fun performSearch() {
        val query = b.etSearch.text.toString().trim()
        val newQuery = if (query.isEmpty()) "tesla" else query
        
        if (newQuery != currentQuery) {
            currentQuery = newQuery
            newsAdapter.updateData(emptyList())
            viewModel.fetchNews(currentQuery, isInitialLoad = true)
        }
    }

    private fun observeViewModel() {
        viewModel.newsResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    b.swipeRefreshLayout.isRefreshing = false
                    val articles = response.data ?: emptyList()
                    if (articles.isEmpty()) {
                        showNoData(true)
                    } else {
                        showNoData(false)
                        newsAdapter.updateData(articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    b.swipeRefreshLayout.isRefreshing = false
                    if (newsAdapter.itemCount == 0) {
                        showNoData(true)
                    }
                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    if (!b.swipeRefreshLayout.isRefreshing) {
                        showProgressBar()
                        showNoData(false)
                    }
                }
            }
        }
    }

    private fun showNoData(isVisible: Boolean) {
        if (isVisible) {
            b.llNoData.visibility = View.VISIBLE
            b.swipeRefreshLayout.visibility = View.GONE
        } else {
            b.llNoData.visibility = View.GONE
            b.swipeRefreshLayout.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(article: ArticleDomainData) {
        val intent = Intent(this, ViewNewsDetailsActivity::class.java)
        intent.putExtra("article", article)
        startActivity(intent)
    }

    private fun hideProgressBar() {
        b.initialProgressBar.visibility = View.GONE
        b.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        if (newsAdapter.itemCount == 0) {
            b.initialProgressBar.visibility = View.VISIBLE
        } else {
            b.paginationProgressBar.visibility = View.VISIBLE
        }
        isLoading = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 5
            
            val shouldPaginate = !isLoading && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.fetchNews(currentQuery)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}
