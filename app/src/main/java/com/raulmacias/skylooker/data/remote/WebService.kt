package com.raulmacias.skylooker.data.remote

import com.google.gson.GsonBuilder
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.data.model.ForecastResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    @GET(value = AppConstants.FORECAST_URL)
    suspend fun getForecast(@Query("q") city: String): ForecastResult

}

object RetrofitClient{
    val webService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}