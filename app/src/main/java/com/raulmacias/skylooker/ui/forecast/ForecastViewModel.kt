package com.raulmacias.skylooker.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.raulmacias.skylooker.application.Resource
import com.raulmacias.skylooker.data.repo.ForecastRepo

class ForecastViewModel(private val repo: ForecastRepo):ViewModel() {

    fun fetchForecast() = liveData{
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.fetchForecast()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

}

class ForecastViewModelFactory(private val repo: ForecastRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ForecastRepo::class.java).newInstance(repo)
    }

}