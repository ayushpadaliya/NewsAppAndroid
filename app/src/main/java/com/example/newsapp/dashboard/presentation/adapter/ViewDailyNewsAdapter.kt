package com.example.newsapp.dashboard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.dashboard.domain.model.ArticleDomainData
import com.example.newsapp.databinding.LayoutItemNewsFeedBinding
import com.example.newsapp.databinding.LayoutLoadingFooterBinding

class ViewDailyNewsAdapter(
    private var articles: MutableList<ArticleDomainData> = mutableListOf(),
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var isLoading = false

    interface OnItemClickListener {
        fun onItemClick(article: ArticleDomainData)
    }

    fun updateData(newArticles: List<ArticleDomainData>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    fun setLoading(loading: Boolean) {
        if (isLoading == loading) return
        isLoading = loading
        if (isLoading) {
            notifyItemInserted(articles.size)
        } else {
            notifyItemRemoved(articles.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == articles.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_ITEM) {
            val b = LayoutItemNewsFeedBinding.inflate(inflater, parent, false)
            ViewHolderNews(b)
        } else {
            val b = LayoutLoadingFooterBinding.inflate(inflater, parent, false)
            ViewHolderLoading(b)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderNews) {
            val article = articles[position]
            holder.b.apply {
                tvNewsTitle.text = article.title
                tvDescription.text = article.description ?: ""
                tvDate.text = article.publishedAt
                tvCategory.text = article.author
                Glide.with(root.context)
                    .load(article.urlToImage)
                    .centerCrop()
                    .into(ivNewsImage)

                root.setOnClickListener {
                    listener.onItemClick(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) articles.size + 1 else articles.size
    }

    class ViewHolderNews(val b: LayoutItemNewsFeedBinding) : RecyclerView.ViewHolder(b.root)
    class ViewHolderLoading(val b: LayoutLoadingFooterBinding) : RecyclerView.ViewHolder(b.root)
}
