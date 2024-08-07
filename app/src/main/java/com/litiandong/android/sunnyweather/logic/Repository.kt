package com.litiandong.android.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.litiandong.android.sunnyweather.TAG
import com.litiandong.android.sunnyweather.logic.dao.PlaceDao
import com.litiandong.android.sunnyweather.logic.model.Place
import com.litiandong.android.sunnyweather.logic.model.Weather
import com.litiandong.android.sunnyweather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavePlace() = PlaceDao.getPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetWork.searchPlace(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Log.i(TAG, "place: ${places}")
            Result.success(places)
        } else {
            Result.failure(RuntimeException("repository status is " +
                    placeResponse.status
            ))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetWork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetWork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)

                Log.i(TAG, "weather: ${weather}")
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                        "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, "error: ", e)
                Result.failure<T>(e)
            }
            emit(result)
        }
}