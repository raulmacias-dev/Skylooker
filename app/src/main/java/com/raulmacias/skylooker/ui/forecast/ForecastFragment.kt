package com.raulmacias.skylooker.ui.forecast


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.raulmacias.skylooker.R
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.Resource
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.remote.RetrofitClient
import com.raulmacias.skylooker.data.repo.ForecastRepoImpl
import com.raulmacias.skylooker.data.source.ForecastDataSource
import com.raulmacias.skylooker.databinding.FragmentForecastBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ForecastFragment : Fragment(R.layout.fragment_forecast) {

    private lateinit var binding: FragmentForecastBinding
    private lateinit var adapter: ForecastAdapter
    private val viewModel by activityViewModels<ForecastViewModel>{
        ForecastViewModelFactory(ForecastRepoImpl(ForecastDataSource(RetrofitClient.webService)))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForecastBinding.bind(view)

        binding.inputFindCityWeather.setOnKeyListener(View.OnKeyListener{ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){

                viewModel.fetchWeather(city = binding.inputFindCityWeather.text.toString() ).observe(viewLifecycleOwner, Observer { result ->
                    when(result){
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.searchbg.visibility = View.VISIBLE
                            binding.mainContainer.visibility = View.GONE
                            Toast.makeText(context, "CARGANDO", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.searchbg.visibility = View.VISIBLE
                            binding.mainContainer.visibility = View.GONE
                            Toast.makeText(context, "CIUDAD NO EXISTE O NO SE PUEDO REALIZAR LA CONSULTA", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success -> {

                            result.data?.let { bindWeather(it) }

                            viewModel.fetchForecast(city = binding.inputFindCityWeather.text.toString()).observe(viewLifecycleOwner, Observer { resultForecast ->
                                when(resultForecast){
                                    is Resource.Loading -> {
                                        binding.progressBar.visibility = View.VISIBLE
                                    }
                                    is Resource.Error -> {
                                        binding.progressBar.visibility = View.GONE
                                    }
                                    is Resource.Success -> {
                                        binding.progressBar.visibility = View.GONE
                                        adapter = ForecastAdapter(resultForecast.data!!.list)
                                        binding.rvForecast.adapter = adapter
                                        binding.inputFindCityWeather.text?.clear()
                                    }
                                }
                            })
                        }
                    }
                })
            }
            false
        })
    }

    private fun bindWeather(data: Forecast){
        binding.progressBar.visibility = View.GONE
        binding.searchbg.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
        Glide.with(requireContext()).load("${AppConstants.BASE_URL_IMG}${data.weather[0].icon}@4x.png").centerCrop().into(binding.imageViewWeather)

        changeImageDetail(data.weather[0].icon)

        binding.address.text = data.name
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
        val date = Date(data.dt * 1000)
        binding.updatedAt.text = sdf.format(date)

        binding.temp.text = "${data.main.temp.roundToInt()} º"
        binding.tempMin.text = "Temp Min: ${data.main.temp_min.roundToInt()} ºC"
        binding.tempMax.text = "Temp Max: ${data.main.temp_max.roundToInt()} ºC"
        val sdfHora = SimpleDateFormat("HH:mm")
        val sunrise = Date(data.sys.sunrise * 1000)
        binding.sunrise.text = sdfHora.format(sunrise)
        val sunset = Date(data.sys.sunset * 1000)
        binding.sunset.text = sdfHora.format(sunset)
    }
    private fun changeImageDetail(iconCode: String?){
        when (iconCode) {
            "01d", "02d", "03d", "04d" -> binding.imageViewDetail.setImageResource(R.drawable.sunny_day)
            "09d", "10d", "11d" -> binding.imageViewDetail.setImageResource(R.drawable.raining_day)
            "13d", "50d" -> binding.imageViewDetail.setImageResource(R.drawable.snowfalling_day)
        }
    }
}