package com.litiandong.android.sunnyweather.logic

import androidx.lifecycle.liveData
import com.litiandong.android.sunnyweather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers

object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetWork.searchPlace(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("repository status is " +
                        placeResponse.status
                ))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }

}