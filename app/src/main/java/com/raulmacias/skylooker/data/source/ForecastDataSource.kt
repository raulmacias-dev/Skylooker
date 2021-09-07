package com.raulmacias.skylooker.data.source


import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.remote.WebService

class ForecastDataSource(private val webService: WebService) {
    suspend fun fetchForecast(): ForecastResult = webService.getForecast("Badajoz", AppConstants.API_KEY)
    suspend fun fetchWeather(): Forecast = webService.getWeather("Badajoz", AppConstants.API_KEY)
}