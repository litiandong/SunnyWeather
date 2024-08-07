package com.litiandong.android.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.litiandong.android.sunnyweather.SunnyWeatherApplication
import com.litiandong.android.sunnyweather.logic.model.Place

const val placeKey = "place"

object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString(placeKey, Gson().toJson(place))
        }
    }

    fun getPlace(): Place {
        val placeJson = sharedPreferences().getString(placeKey, "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context
        .getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}