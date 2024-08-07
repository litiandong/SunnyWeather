package com.litiandong.android.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.litiandong.android.sunnyweather.databinding.PlaceItemBinding
import com.litiandong.android.sunnyweather.logic.model.Place
import com.litiandong.android.sunnyweather.ui.weather.Location_Lat
import com.litiandong.android.sunnyweather.ui.weather.Location_Lng
import com.litiandong.android.sunnyweather.ui.weather.Place_Name
import com.litiandong.android.sunnyweather.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeName: TextView
        val placeAddress: TextView
        init {
            placeName = binding.placeName
            placeAddress = binding.placeAddress
        }

        fun bind(place: Place) {
            placeName.text = place.name
            placeAddress.text = place.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        binding.root.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra(Location_Lng, place.location.lng)
                putExtra(Location_Lat, place.location.lat)
                putExtra(Place_Name, place.name)
            }
            fragment.viewModel.savePlace(place)
            fragment.startActivity(intent)
            fragment.activity?.finish()
        }
        return holder
    }

    override fun getItemCount() = placeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.bind(place)
    }
}