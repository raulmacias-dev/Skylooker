package com.raulmacias.skylooker.ui.forecast

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.raulmacias.skylooker.R
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.Resource
import com.raulmacias.skylooker.data.model.WeatherResult
import com.raulmacias.skylooker.data.remote.RetrofitClient
import com.raulmacias.skylooker.data.repo.ForecastRepoImpl
import com.raulmacias.skylooker.data.repo.WeatherRepoImpl
import com.raulmacias.skylooker.data.source.ForecastDataSource
import com.raulmacias.skylooker.data.source.WeatherDataSource
import com.raulmacias.skylooker.databinding.FragmentForecastBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ForecastFragment : Fragment(R.layout.fragment_forecast) {

    private lateinit var binding: FragmentForecastBinding
    private lateinit var adapter: ForecastAdapter
    private val viewModel by activityViewModels<ForecastViewModel>{
        ForecastViewModelFactory(
            ForecastRepoImpl(ForecastDataSource(RetrofitClient.webService)),
            WeatherRepoImpl(WeatherDataSource(RetrofitClient.webService)),
            fragment = this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForecastBinding.bind(view)

        binding.inputFindCityWeather.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                fetchData("")
            }
            false
        }

        viewModel.location.observe(viewLifecycleOwner, {location ->
            if (location.isNotBlank()){
                binding.textViewLocation.text = getString(R.string.location_found, location)
                fetchData(locationName = location)
            }else{
                binding.textViewLocation.text = getString(R.string.location_not_fount)
            }
        })

        viewModel.getLocation()
    }

    private fun fetchData(locationName: String){

        val location : String = if (locationName.isBlank()){
            binding.inputFindCityWeather.text.toString()
        }else{
            locationName
        }

        viewModel.fetchWeather(city = location ).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.searchbg.visibility = View.VISIBLE
                    binding.mainContainer.visibility = View.GONE
                    Toast.makeText(context, "Cargando...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.searchbg.visibility = View.VISIBLE
                    binding.mainContainer.visibility = View.GONE
                    Toast.makeText(context, "Ciudad no existe o la consulta no obtuvo respuesta", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {

                    result.data?.let { bindWeather(it) }

                    viewModel.fetchForecast(city = location).observe(viewLifecycleOwner,
                        { resultForecast ->
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
    private fun bindWeather(data: WeatherResult){
        binding.progressBar.visibility = View.GONE
        binding.searchbg.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE

        Glide.with(requireContext()).load("${AppConstants.BASE_URL_IMG}${data.weather[0].icon}@4x.png").centerCrop().into(binding.imageViewWeather)

        when(data.weather[0].icon){
            "01d", "02d"-> binding.imageViewDetail.setImageResource(R.drawable.sunny_day)
            "03d", "04d" -> binding.imageViewDetail.setImageResource(R.drawable.cloudy_day)
            "09d", "10d", "11d" -> binding.imageViewDetail.setImageResource(R.drawable.raining_day)
            "13d", "50d" -> binding.imageViewDetail.setImageResource(R.drawable.snowfalling_day)
        }

        binding.address.text = data.name

        val sdf = SimpleDateFormat(getString(R.string.format_fecha), Locale.getDefault())
        val date = Date(data.dt * 1000)
        binding.updatedAt.text = sdf.format(date)

        binding.temp.text =  getString(R.string.temp, data.main.temp.roundToInt())
        binding.tempMin.text = getString(R.string.temp_min_max,"Min", data.main.temp_min.roundToInt())
        binding.tempMax.text = getString(R.string.temp_min_max,"Max", data.main.temp_min.roundToInt())

        val sdfHora = SimpleDateFormat(getString(R.string.format_hora), Locale.getDefault())
        val sunrise = Date(data.sys.sunrise * 1000)
        binding.sunrise.text = sdfHora.format(sunrise)
        val sunset = Date(data.sys.sunset * 1000)
        binding.sunset.text = sdfHora.format(sunset)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out kotlin.String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            AppConstants.LOCATION_REQUEST -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.getLocation()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}