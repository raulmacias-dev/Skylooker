package com.raulmacias.skylooker.data.remote

import com.google.gson.GsonBuilder
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.model.WeatherResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {

    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") id: String,
        @Query("units") units: String
    ): ForecastResult

    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") id: String,
        @Query("units") units: String
    ): WeatherResult

}

object RetrofitClient{
    val webService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}