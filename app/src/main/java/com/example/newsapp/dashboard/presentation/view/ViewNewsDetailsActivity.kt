package com.example.newsapp.dashboard.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.newsapp.dashboard.domain.model.ArticleDomainData
import com.example.newsapp.databinding.ActivityViewNewsDetailsBinding

class ViewNewsDetailsActivity : AppCompatActivity() {

    private lateinit var b: ActivityViewNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityViewNewsDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)
        val article = intent.getSerializableExtra("article") as? ArticleDomainData
        article?.let { populateData(it) }
        b.ivBack.setOnClickListener { finish() }
    }

    private fun populateData(article: ArticleDomainData) {
        b.apply {
            tvTitle.text = article.title
            tvSource.text = article.source.name
            tvAuthor.text = article.author ?: "Unknown Author"
            tvDate.text = article.publishedAt
            tvDescription.text = article.description ?: article.content ?: ""

            Glide.with(this@ViewNewsDetailsActivity)
                .load(article.urlToImage)
                .centerCrop()
                .into(ivNewsImage)

            btnReadFull.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                startActivity(intent)
            }
        }
    }
}
