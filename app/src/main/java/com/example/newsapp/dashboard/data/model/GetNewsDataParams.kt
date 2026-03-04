package com.example.newsapp.dashboard.data.model

class GetNewsDataParams(
    val query: String,
    val from: String,
    val sortBy: String,
    val apiKey: String,
    val page: Int
)
