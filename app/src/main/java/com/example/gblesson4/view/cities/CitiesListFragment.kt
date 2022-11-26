package com.example.gblesson4.view.cities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gblesson4.R
import com.example.gblesson4.model.databinding.FragmentCitiesListBinding

import com.example.gblesson4.model.Location.Russia
import com.example.gblesson4.model.Location.World
import com.example.gblesson4.model.Weather
import com.example.gblesson4.utils.SP_KEY_LOCATION
import com.example.gblesson4.utils.SP_REGION_SETTINGS
import com.example.gblesson4.view.details.WeatherFragmentDetails

import com.example.gblesson4.viewmodel.AppState
import com.example.gblesson4.viewmodel.WeatherViewModelList

import com.google.android.material.snackbar.Snackbar

class CitiesListFragment : Fragment() {

    private var _binding: FragmentCitiesListBinding? = null
    private val binding get() = _binding!!
    private var sharedPreferences: SharedPreferences? = null
    private var location = Russia // Временное решение, потом локацию будем получать от Android

    private val viewModel: WeatherViewModelList by lazy {
        ViewModelProvider(this)[WeatherViewModelList::class.java]
    }

    private val adapter = CitiesFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, WeatherFragmentDetails.newInstance(
                        Bundle().apply {
                            putParcelable(WeatherFragmentDetails.BUNDLE_EXTRA, weather)
                        }
                    ))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
    })

    companion object {
        fun newInstance() = CitiesListFragment()
    }

    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences = context?.getSharedPreferences(SP_REGION_SETTINGS, Context.MODE_PRIVATE)
        location = when(sharedPreferences?.getBoolean(SP_KEY_LOCATION, true)) {
            true -> Russia
            false -> World
            else -> Russia
        }

        _binding = FragmentCitiesListBinding.inflate(inflater, container, false)

        when (location) {
            Russia -> binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
            World -> binding.mainFragmentFAB.setImageResource(R.drawable.ic_world)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener { changeDataSet() }

        viewModel.getLiveData().observe(viewLifecycleOwner) { appState -> renderData(appState) }
        viewModel.getWeather(location)
    }

    private fun renderData(appState: AppStateLocal) = when (appState) {
        is AppStateLocal.Success -> {
            adapter.setWeather(appState.weatherData)
        }
        is AppStateLocal.Error -> {
            with(binding) {
                citiesFragmentRootLayout.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeather(location) })
            }
        }
        else -> {}
    }.also {
        if (appState == AppStateLocal.Loading) binding.citiesFragmentLoadingLayout.visibility =
            View.VISIBLE
        else binding.citiesFragmentLoadingLayout.visibility = View.GONE
    }

    private fun changeDataSet() {
        location = !location
        viewModel.getWeather(location)
        when (location) {
            Russia -> binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
            World -> binding.mainFragmentFAB.setImageResource(R.drawable.ic_world)
        }

        sharedPreferences?.apply {
            edit()
                .putBoolean(SP_KEY_LOCATION, when(location) {
                    Russia -> true
                    World -> false })
                .apply()
        }
    }


    override fun onDestroy() {
        _binding = null
        adapter.removeListener()
        super.onDestroy()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }
}