package com.litiandong.android.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.litiandong.android.sunnyweather.TAG
import com.litiandong.android.sunnyweather.databinding.FragmentPlaceBinding
import com.litiandong.android.sunnyweather.ui.weather.Location_Lat
import com.litiandong.android.sunnyweather.ui.weather.Location_Lng
import com.litiandong.android.sunnyweather.ui.weather.Place_Name
import com.litiandong.android.sunnyweather.ui.weather.WeatherActivity

class PlaceFragment : Fragment() {

    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private var _binding: FragmentPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    private val recyclerView get() = binding.recyclerView
    private lateinit var adapter: PlaceAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)

        recyclerView.adapter = adapter

        if (viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra(Location_Lng, place.location.lng)
                putExtra(Location_Lat, place.location.lat)
                putExtra(Place_Name, place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到地点信息", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "error: ", result.exceptionOrNull())
            }
        })

    }

}