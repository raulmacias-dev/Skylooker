package com.raulmacias.skylooker.data.source

import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.data.model.WeatherResult
import com.raulmacias.skylooker.data.remote.WebService

class WeatherDataSource(private val webService: WebService) {

    suspend fun fetchWeather(city: String): WeatherResult {
        return webService.getWeather(city, AppConstants.API_KEY,"metric")
    }

}