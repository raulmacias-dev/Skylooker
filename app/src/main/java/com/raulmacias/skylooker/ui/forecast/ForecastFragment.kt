package com.raulmacias.skylooker.ui.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.raulmacias.skylooker.R
import com.raulmacias.skylooker.application.Resource
import com.raulmacias.skylooker.data.remote.RetrofitClient
import com.raulmacias.skylooker.data.repo.ForecastRepoImpl
import com.raulmacias.skylooker.data.source.ForecastDataSource
import com.raulmacias.skylooker.databinding.FragmentForecastBinding


class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private lateinit var binding: FragmentForecastBinding
    private lateinit var adapter: ForecastAdapter
    private val viewModel by activityViewModels<ForecastViewModel>{
        ForecastViewModelFactory(ForecastRepoImpl(ForecastDataSource(RetrofitClient.webService)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForecastBinding.bind(view)

        viewModel.fetchForecast().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(context, "LOADING", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter = result.data?.let { ForecastAdapter(it.list) }!!
                    binding.rvForecast.adapter = adapter
                    Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}