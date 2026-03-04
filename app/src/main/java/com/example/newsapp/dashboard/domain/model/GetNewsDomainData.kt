package com.example.newsapp.dashboard.domain.model

import java.io.Serializable

data class GetNewsDomainData(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDomainData>
) : Serializable

data class ArticleDomainData(
    val source: SourceDomainData,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
) : Serializable

data class SourceDomainData(
    val id: String?,
    val name: String?
) : Serializable
