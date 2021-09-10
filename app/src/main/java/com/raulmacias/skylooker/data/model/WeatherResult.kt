package com.raulmacias.skylooker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class WeatherResult (
    @PrimaryKey
    @SerializedName("id") val id : Int,
    @SerializedName("coord") val coord : Coord,
    @SerializedName("weather") val weather : List<Weather>,
    @SerializedName("base") val base: String,
    @SerializedName("main") val main : Main,
    @SerializedName("visibility") val visibility : Int,
    @SerializedName("wind") val wind : Wind,
    @SerializedName("clouds") val clouds : Clouds,
    @SerializedName("dt") val dt : Long,
    @SerializedName("sys") val sys : SysWeather,
    @SerializedName("name") val name : String,
    @SerializedName("timezone") val pop : Int,
    @SerializedName("cod") val dt_txt : Int,

    )

data class SysWeather (
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: Int,
    @SerializedName("country") val pod : String,
    @SerializedName("sunrise") val sunrise : Long,
    @SerializedName("sunset") val sunset : Long
)