package com.raulmacias.skylooker.data.source



import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.model.WeatherResult
import com.raulmacias.skylooker.data.remote.WebService

class ForecastDataSource(private val webService: WebService) {

    suspend fun fetchForecast(city: String): ForecastResult{
        return webService.getForecast(city, AppConstants.API_KEY, "metric")
    }
    suspend fun fetchWeather(city: String): WeatherResult{
        return webService.getWeather(city, AppConstants.API_KEY,"metric")
    }

}