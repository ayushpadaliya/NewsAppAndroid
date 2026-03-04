package com.example.newsapp.dashboard.data.repository

import com.example.newsapp.dashboard.data.model.GetNewsDataParams
import com.example.newsapp.dashboard.domain.mapper.GetNewsDataMapper
import com.example.newsapp.dashboard.domain.model.GetNewsDomainData
import com.example.newsapp.network.ApiServices
import com.example.newsapp.utils.PreferenceManager

class GetNewsDetailsRepository(
    private val apiServices: ApiServices,
    private val mapper: GetNewsDataMapper,
    private val preferenceManager: PreferenceManager
) {

    suspend fun getNewsData(params: GetNewsDataParams): GetNewsDomainData? {
        return try {
            val response = apiServices.getNewsData(
                query = params.query,
                sortBy = params.sortBy,
                apiKey = params.apiKey,
                page = params.page
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    preferenceManager.setNewsApiData(it)
                    mapper.mapToDomain(it)
                }
            } else {
                fetchFromCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fetchFromCache()
        }
    }

    private fun fetchFromCache(): GetNewsDomainData? {
        return preferenceManager.getNewsApiData()?.let {
            mapper.mapToDomain(it)
        }
    }
}
