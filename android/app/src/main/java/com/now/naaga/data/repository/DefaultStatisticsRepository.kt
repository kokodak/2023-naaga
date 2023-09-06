package com.now.naaga.data.repository

import com.now.domain.model.Statistics
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.remote.retrofit.ServicePool.statisticsService
import com.now.naaga.data.remote.retrofit.fetchResponse

class DefaultStatisticsRepository : StatisticsRepository {
    override fun getMyStatistics(callback: (Result<Statistics>) -> Unit) {
        val call = statisticsService.getMyStatistics()
        call.fetchResponse(
            onSuccess = { statisticsDto ->
                callback(Result.success(statisticsDto.toDomain()))
            },
            onFailure = {
                callback(Result.failure(it))
            },
        )
    }
}