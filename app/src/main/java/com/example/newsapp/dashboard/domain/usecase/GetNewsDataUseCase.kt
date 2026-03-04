package com.example.newsapp.dashboard.domain.usecase

import com.example.newsapp.dashboard.data.model.GetNewsDataParams
import com.example.newsapp.dashboard.data.repository.GetNewsDetailsRepository
import com.example.newsapp.dashboard.domain.model.GetNewsDomainData

class GetNewsDataUseCase(
    private val repository: GetNewsDetailsRepository
) {
    suspend fun getNewsData(params: GetNewsDataParams): GetNewsDomainData? {
        return repository.getNewsData(params)
    }
}
