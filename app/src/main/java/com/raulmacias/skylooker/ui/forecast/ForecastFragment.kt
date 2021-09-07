package com.raulmacias.skylooker.ui.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.raulmacias.skylooker.R
import com.raulmacias.skylooker.application.AppConstants
import com.raulmacias.skylooker.application.Resource
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

        viewModel.fetchWeather().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(context, "LOADING", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "ERROR: ${result.message.toString()}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Glide.with(requireContext()).load("${AppConstants.BASE_URL_IMG}${result.data!!.weather[0].icon}@4x.png").centerCrop().into(binding.imageViewWeather)
                    binding.address.text = result.data!!.name
                    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
                    val date = Date(result.data!!.dt * 1000)
                    binding.updatedAt.text = sdf.format(date)

                    binding.temp.text = "${((result.data!!.main.temp_kf * 9/ 5) + 32).roundToInt()} ºC"
                }
            }
        })

        viewModel.fetchForecast().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(context, "LOADING", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "ERROR: ${result.message.toString()}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter = ForecastAdapter(result.data!!.list)
                    binding.rvForecast.adapter = adapter
                    //Toast.makeText(context, "SUCCESS ${result.data.list}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}