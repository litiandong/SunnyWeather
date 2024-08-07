package com.litiandong.android.sunnyweather.ui.weather

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.litiandong.android.sunnyweather.TAG
import com.litiandong.android.sunnyweather.databinding.ActivityWeatherBinding
import com.litiandong.android.sunnyweather.databinding.ForecastItemBinding
import com.litiandong.android.sunnyweather.logic.model.Weather
import com.litiandong.android.sunnyweather.logic.model.getSky
import java.util.Locale

const val Location_Lng = "location_lng"
const val Location_Lat = "location_lat"
const val Place_Name = "place_name"

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        window.statusBarColor = Color.TRANSPARENT

        setContentView(binding.root)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        Log.i(TAG, "weather activity: " +
         viewModel.placeName + viewModel.locationLat + " " + viewModel.locationLng)

        viewModel.refreshWeather()

        viewModel.weatherLiveData.observe(this, Observer {  result->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "error: ", result.exceptionOrNull())
            }
        })
    }

    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val daily = weather.daily


        try {
            // now.xml
            binding.now.placeName.text = viewModel.placeName
            binding.now.currentAqi.text = realtime.airQuality.aqi.toString()
            binding.now.currentTemp.text = "${realtime.temperature.toInt()} ℃"
            val now_sky_info = getSky(realtime.skycon)
            binding.now.currentSky.text = now_sky_info.info
            val now_bg = now_sky_info.bg
            binding.now.root.setBackgroundResource(now_bg)

            // forecast.xml
            binding.forever.forecastLayout.removeAllViews()

            for (i in 0 until daily.skycon.size) {
                val skycon = daily.skycon[i]
                val temperature = daily.temperature[i]
                val sky = getSky(skycon.value)
                val forecastItemBinding: ForecastItemBinding = ForecastItemBinding.inflate(layoutInflater)

                forecastItemBinding.skyIcon.setImageResource(sky.icon)
                forecastItemBinding.skyInfo.text = sky.info
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                forecastItemBinding.dataInfo.text = simpleDateFormat.format(skycon.date)
                val temperatureText = "${temperature.min} ~ ${temperature.max} ℃"
                forecastItemBinding.temperatureInfo.text = temperatureText
                binding.forever.forecastLayout.addView(forecastItemBinding.root)
            }
            val lifeIndex = daily.lifeIndex
            binding.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
            binding.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
            binding.lifeIndex.carWashingText.text = lifeIndex.dressing[0].desc
            binding.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
            Log.i(TAG, "show info over")
            binding.root.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e(TAG, "error: ", e)
        }
    }
}