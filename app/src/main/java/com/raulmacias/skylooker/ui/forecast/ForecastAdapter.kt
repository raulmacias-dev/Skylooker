package com.raulmacias.skylooker.ui.forecast

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.BaseViewHolder
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.databinding.ForecastItemBinding

class ForecastAdapter(
    private val forecastList: List<Forecast>
): RecyclerView.Adapter<BaseViewHolder<*>>() {

    private inner class ForecastViewHolder(
        val binding: ForecastItemBinding,
        val context: Context
    ): BaseViewHolder<Forecast>(binding.root) {
        override fun bind(item: Forecast) {
            binding.textViewForecast.text = item.weather[0].main
            //"http://openweathermap.org/img/w/" + iconcode + ".png"
            Glide.with(context).load("${AppConstants.BASE_URL}img/w/${item.weather[0].icon}").centerCrop().into(binding.imageForecast)
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