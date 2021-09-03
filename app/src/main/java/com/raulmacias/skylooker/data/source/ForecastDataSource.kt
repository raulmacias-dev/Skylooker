package com.raulmacias.skylooker.data.source


import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.remote.WebService

class ForecastDataSource(val webService: WebService) {
    suspend fun fetchForecast(): ForecastResult{
        return webService.getForecast("Badajoz")
    }
}