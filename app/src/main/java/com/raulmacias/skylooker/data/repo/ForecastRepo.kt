package com.raulmacias.skylooker.data.repo

import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.source.ForecastDataSource

interface ForecastRepo {
    suspend fun fetchForecast(): ForecastResult
    suspend fun fetchWeather(): Forecast
}

class ForecastRepoImpl(private val dataSource: ForecastDataSource):ForecastRepo{
    override suspend fun fetchForecast(): ForecastResult = dataSource.fetchForecast()
    override suspend fun fetchWeather():Forecast = dataSource.fetchWeather()
}