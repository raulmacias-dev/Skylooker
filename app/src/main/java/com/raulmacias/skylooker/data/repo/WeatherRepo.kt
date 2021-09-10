package com.raulmacias.skylooker.data.repo

import com.raulmacias.skylooker.data.model.WeatherResult
import com.raulmacias.skylooker.data.source.WeatherDataSource

interface WeatherRepo {
    suspend fun fetchWeather(city: String): WeatherResult
}

class WeatherRepoImpl(private val dataSource: WeatherDataSource):WeatherRepo{
    override suspend fun fetchWeather(city: String): WeatherResult = dataSource.fetchWeather(city)
}