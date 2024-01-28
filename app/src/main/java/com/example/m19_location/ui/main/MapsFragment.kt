package com.example.m19_location.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.m19_location.R
import com.example.m19_location.databinding.FragmentMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        with(googleMap.uiSettings) {
            this.isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
        }

        googleMap.isMyLocationEnabled = true

        googleMap.setLocationSource(object : LocationSource {
            var locationListener: LocationSource.OnLocationChangedListener? = null
            override fun activate(p0: LocationSource.OnLocationChangedListener) {
                locationListener = p0
            }

            override fun deactivate() {
                locationListener = null
            }
        })
    }

}