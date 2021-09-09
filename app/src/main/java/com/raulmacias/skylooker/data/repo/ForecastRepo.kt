package com.raulmacias.skylooker.data.repo

import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.source.ForecastDataSource

interface ForecastRepo {
    suspend fun fetchForecast(city: String): ForecastResult
    suspend fun fetchWeather(city: String): Forecast
}

class ForecastRepoImpl(private val dataSource: ForecastDataSource):ForecastRepo{
    override suspend fun fetchForecast(city: String): ForecastResult = dataSource.fetchForecast(city)
    override suspend fun fetchWeather(city: String):Forecast = dataSource.fetchWeather(city)
}