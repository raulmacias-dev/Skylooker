package com.raulmacias.skylooker.ui.forecast

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.BaseViewHolder
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.databinding.ForecastItemBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.math.roundToInt


class ForecastAdapter(
    private val forecastList: List<Forecast>
): RecyclerView.Adapter<BaseViewHolder<*>>() {

    private inner class ForecastViewHolder(
        val binding: ForecastItemBinding,
        val context: Context
    ): BaseViewHolder<Forecast>(binding.root) {
        override fun bind(item: Forecast) {
            binding.textViewForecast.text = "${((item.main.temp_kf * 9/ 5) + 32).roundToInt()} º"

            Glide.with(context).load("${AppConstants.BASE_URL_IMG}${item.weather[0].icon}@4x.png").centerCrop().into(binding.imageForecast)

            val sdf = SimpleDateFormat("HH:mm")
            val date = Date(item.dt * 1000)
            binding.textViewDate.text = sdf.format(date)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ForecastViewHolder -> holder.bind(forecastList[position])
        }
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }
}