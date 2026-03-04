package com.example.newsapp.dashboard.domain.mapper

import com.example.newsapp.dashboard.data.model.Article
import com.example.newsapp.dashboard.data.model.GetNewsDetailsResponse
import com.example.newsapp.dashboard.data.model.Source
import com.example.newsapp.dashboard.domain.model.ArticleDomainData
import com.example.newsapp.dashboard.domain.model.GetNewsDomainData
import com.example.newsapp.dashboard.domain.model.SourceDomainData

class GetNewsDataMapper {

    fun mapToDomain(response: GetNewsDetailsResponse): GetNewsDomainData {
        return GetNewsDomainData(
            status = response.status,
            totalResults = response.totalResults,
            articles = response.articles.map { it.toDomain() }
        )
    }

    private fun Article.toDomain(): ArticleDomainData {
        return ArticleDomainData(
            source = this.source.toDomain(),
            author = this.author,
            title = this.title,
            description = this.description,
            url = this.url,
            urlToImage = this.urlToImage,
            publishedAt = this.publishedAt,
            content = this.content
        )
    }

    private fun Source.toDomain(): SourceDomainData {
        return SourceDomainData(
            id = this.id,
            name = this.name
        )
    }
}
