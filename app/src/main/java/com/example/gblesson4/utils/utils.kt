package com.example.gblesson4.utils

import com.example.gblesson4.model.City
import com.example.gblesson4.model.Weather
import com.example.gblesson4.model.dto.WeatherDTO


class Utils {
}

fun convertDtoToWeather(weatherDTO: WeatherDTO): Weather {
    return Weather(
        City(
            getAddress(weatherDTO.info.lat, weatherDTO.info.lon),
            weatherDTO.info.lat, weatherDTO.info.lon),
        temperature = weatherDTO.fact.temp,
        feelsLike = weatherDTO.fact.feelsLike,
        icon = weatherDTO.fact.icon
    )
}

fun convertWeatherToEntity(weather: Weather): WeatherEntity {
    return weather.let {
        WeatherEntity(0, it.city.name, it.city.lat, it.city.lon,
            it.temperature, it.feelsLike, it.icon)
    }
}

fun convertEntityToWeather(entity: List<WeatherEntity>): List<Weather> {
    return entity.map {
        Weather(City(it.name, it.lat, it.lon), it.temperature, it.feelsLike, it.icon)
    }
}