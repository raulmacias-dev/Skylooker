package com.raulmacias.skylooker.ui.forecast

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raulmacias.skylooker.MainActivity
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.Resource
import com.raulmacias.skylooker.data.model.Forecast
import com.raulmacias.skylooker.data.model.ForecastResult
import com.raulmacias.skylooker.data.model.WeatherResult
import com.raulmacias.skylooker.data.repo.ForecastRepo
import com.raulmacias.skylooker.data.repo.WeatherRepo
import kotlinx.coroutines.Dispatchers
import java.util.*

class ForecastViewModel(
    private val forecastRepo: ForecastRepo,
    private val weatherRepo: WeatherRepo,
    private val fragment: Fragment):ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext())

    var location = MutableLiveData<String>("")

    fun fetchWeather(city: String) = liveData<Resource<WeatherResult>>(viewModelScope.coroutineContext + Dispatchers.Main) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(weatherRepo.fetchWeather(city)))
        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun fetchForecast(city: String) = liveData<Resource<ForecastResult>>(viewModelScope.coroutineContext + Dispatchers.Main){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(forecastRepo.fetchForecast(city)))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

     fun getLocation(){

        if (ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                AppConstants.LOCATION_REQUEST
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(fragment.requireActivity()) { location ->
                    if (location != null) {
                        val geocoder = Geocoder(fragment.requireContext(), Locale.getDefault())
                        var addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        Log.d("dev_", "adress: ${addresses[0].locality}")
                        this.location.value = addresses[0].locality
                    } else {
                        Log.d("dev_", "not location")
                        this.location.value = ""
                    }
                }
        }
    }

}

class ForecastViewModelFactory(
    private val forecastRepo: ForecastRepo,
    private val weatherRepo: WeatherRepo,
    private val fragment: Fragment): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ForecastRepo::class.java,WeatherRepo::class.java, Fragment::class.java).newInstance(forecastRepo, weatherRepo, fragment)
    }
}