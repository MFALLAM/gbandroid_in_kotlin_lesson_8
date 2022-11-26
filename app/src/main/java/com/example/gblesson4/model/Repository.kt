package com.example.gblesson4.model

fun interface Repository {

    fun getWeather(hasInternet: Boolean, location: Location): List<Weather>
}

class RepositoryImpl : Repository {

    override fun getWeather(
        hasInternet: Boolean,
        location: Location): List<Weather> = when (hasInternet) {
        true -> getWeatherFromServer()
        else -> when (location) {
            Location.World -> getWeatherFromLocalSourceWorld()
            Location.Russia -> getWeatherFromLocalSourceRus()
        }}


    // Ответ от сервера в любом случе будет списком
    private fun getWeatherFromServer(): List<Weather> = listOf(Weather())

    private fun getWeatherFromLocalSourceRus(): List<Weather> = getRussianCities()

    private fun getWeatherFromLocalSourceWorld(): List<Weather> = getWorldCities()
}