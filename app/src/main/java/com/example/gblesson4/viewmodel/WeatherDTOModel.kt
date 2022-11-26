package com.example.gblesson4.viewmodel

import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gblesson4.model.City
import com.example.gblesson4.model.dto.WeatherDTO
import com.example.gblesson4.utils.getLines
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Handler
import javax.net.ssl.HttpsURLConnection


class WeatherDTOModel(
    private val liveDataDTO: MutableLiveData<AppState> = MutableLiveData(),
) : ViewModel() {

    fun getLiveDataDTO() = liveDataDTO

    fun getWeather(city: City) = getWeatherFromServer(city)


    private fun getWeatherFromServer(city: City) {
        liveDataDTO.value = AppState.Loading
        //requestToServer(city)
        //okhttpRequestToServer(city)
        val detailsRepositoryImpl = DetailsRepositoryImpl(RemoteDataSource())
        detailsRepositoryImpl.getWeatherDetailsFromServer(city.lat, city.lon, callBack)
    }

    private val callBack = object : retrofit2.Callback<WeatherDTO> {
        override fun onResponse(
            call: retrofit2.Call<WeatherDTO>,
            response: retrofit2.Response<WeatherDTO>
        ) {
            val serverResponse: WeatherDTO? = response.body()

            liveDataDTO.postValue(
                if(response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                })
        }

        override fun onFailure(call: retrofit2.Call<WeatherDTO>, t: Throwable) {
            liveDataDTO.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: WeatherDTO) : AppState {
            val fact = serverResponse.fact
            return if (fact == null || fact.temp == null || fact.feelsLike == null ||
                fact.condition.isNullOrEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                //AppState.Success(convertDtoToModel(serverResponse))
                AppState.SuccessFromServer(serverResponse)
            }
        }
    }

    private fun okhttpRequestToServer(city: City) {
        val client = OkHttpClient()
        val builder: Request.Builder = Request.Builder().apply {
            header(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
            url(YANDEX_LINK + "lat=${city.lat}&lon=${city.lon}")
        }
        val request: Request = builder.build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val serverResponse: String? = response.body?.string()
                if (response.isSuccessful && serverResponse != null) {
                    val weather: WeatherDTO =
                        Gson().fromJson(serverResponse, WeatherDTO::class.java)
                    Handler(Looper.getMainLooper()).post {
                        liveDataDTO.postValue(AppState.SuccessFromServer(weather))
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        // не нравится мне эта строка, и студии не нравится ))
                        liveDataDTO.postValue(AppState.Error(throw Exception("unable get weather")))
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    liveDataDTO.postValue(AppState.Error(e))
                }
            }
        })
    }

    private fun requestToServer(city: City) {
        var uri: URL? = null
        try {
            uri =
                URL("${YANDEX_LINK}lat=${city.lat}&lon=${city.lon}")
        } catch (e: MalformedURLException) {
            liveDataDTO.value = AppState.Error(e)
            Log.e("@@@", "Fail URI", e)
            e.printStackTrace()
        }

        var connection: HttpsURLConnection? = null

        try {

            connection = uri?.openConnection() as HttpsURLConnection
            connection.also {
                it.readTimeout = 5000
                it.addRequestProperty(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
            }

            Thread {
                val reader: BufferedReader
                try {
                    reader = BufferedReader(InputStreamReader(connection.inputStream))
                } catch (e: RuntimeException) {
                    liveDataDTO.postValue(AppState.Error(e))
                    Log.e("@@@", "Cannot receive data from server", e)
                    e.printStackTrace()
                    return@Thread
                }
                val weather: WeatherDTO
                try {
                    weather = Gson().fromJson(getLines(reader), WeatherDTO::class.java)
                } catch (e: JsonSyntaxException) {
                    liveDataDTO.postValue(AppState.Error(e))
                    Log.e("@@@", "Cannot receive data from server", e)
                    e.printStackTrace()
                    return@Thread
                }
                Handler(Looper.getMainLooper()).post {
                    liveDataDTO.postValue(AppState.SuccessFromServer(weather))
                }
            }.start()
        } catch (e: RuntimeException) {
            liveDataDTO.postValue(AppState.Error(e))
            Log.e("@@@", "Cannot receive data from server", e)
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

    }
}