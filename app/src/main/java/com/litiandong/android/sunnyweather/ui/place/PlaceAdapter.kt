package com.litiandong.android.sunnyweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.litiandong.android.sunnyweather.R
import com.litiandong.android.sunnyweather.databinding.PlaceItemBinding
import com.litiandong.android.sunnyweather.logic.Place

class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) :
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
        return ViewHolder(binding)
    }

    override fun getItemCount() = placeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.bind(place)
    }
}