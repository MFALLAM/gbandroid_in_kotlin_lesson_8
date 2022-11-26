package com.example.gblesson4.viewmodel
import com.example.gblesson4.model.Weather
import com.example.gblesson4.model.dto.WeatherDTO

sealed class AppState {
    data class Success(val weather: Weather) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}