package com.raulmacias.skylooker.ui.forecast


import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
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
import com.google.android.gms.location.LocationServices
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


class ForecastFragment : Fragment(com.raulmacias.skylooker.R.layout.fragment_forecast) {

    private lateinit var binding: FragmentForecastBinding
    private lateinit var adapter: ForecastAdapter
    private val viewModel by activityViewModels<ForecastViewModel>{
        ForecastViewModelFactory(ForecastRepoImpl(ForecastDataSource(RetrofitClient.webService)))
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForecastBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()

        binding.inputFindCityWeather.setOnKeyListener(View.OnKeyListener{ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                fetchData("")
            }
            false
        })
    }

    private fun fetchData(locationName: String){

        var location : String = if (locationName.isBlank()){
            binding.inputFindCityWeather.text.toString()
        }else{
            locationName
        }

        viewModel.fetchWeather(city = location ).observe(viewLifecycleOwner, Observer { result ->
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

                    viewModel.fetchForecast(city = location).observe(viewLifecycleOwner, Observer { resultForecast ->
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
            "01d", "02d", "03d", "04d" -> binding.imageViewDetail.setImageResource(com.raulmacias.skylooker.R.drawable.sunny_day)
            "09d", "10d", "11d" -> binding.imageViewDetail.setImageResource(com.raulmacias.skylooker.R.drawable.raining_day)
            "13d", "50d" -> binding.imageViewDetail.setImageResource(com.raulmacias.skylooker.R.drawable.snowfalling_day)
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AppConstants.LOCATION_REQUEST
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        var addresses: List<Address> = emptyList()
                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            // In this sample, we get just a single address.
                            1)
                        fetchData(addresses[0].locality)
                        binding.textViewLocation.text = "Tú ubicación: ${addresses[0].locality}"
                        Log.d("dev_", "adress: ${addresses[0].locality}")
                    } else {
                        binding.textViewLocation.text = "No encontrada última ubicación"
                        Log.d("dev_", "not location")
                    }
                }
        }
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
                    getLocation()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}