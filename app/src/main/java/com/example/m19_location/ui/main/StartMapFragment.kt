package com.example.m19_location.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.m19_location.R
import com.example.m19_location.databinding.FragmentMapsBinding
import com.example.m19_location.ui.main.MainViewModel.Companion.Alcatraz
import com.example.m19_location.ui.main.MainViewModel.Companion.californiaAcademyOfSciences
import com.example.m19_location.ui.main.MainViewModel.Companion.goldenGateBridge
import com.example.m19_location.ui.main.MainViewModel.Companion.siliconValley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class StartMapFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private lateinit var fusedClient: FusedLocationProviderClient
    var locationListener: LocationSource.OnLocationChangedListener? = null

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (map.values.isNotEmpty() && map.values.all { it }) {
            startLocation()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation?.let {
                locationListener?.onLocationChanged(it)
            }
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun checkPermissions() {
        if (REQUIRED_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            startLocation()
        } else {
            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocation() {
        map?.isMyLocationEnabled = true
        val request = LocationRequest
            .Builder(2000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(layoutInflater)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            map = it
            checkPermissions()
            with(it.uiSettings) {
                this.isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
            }
            it.setLocationSource(object : LocationSource {
                override fun activate(location: LocationSource.OnLocationChangedListener) {
                    locationListener = location
                }

                override fun deactivate() {
                    locationListener = null
                }
            })

            it.addMarker(MarkerOptions().position(siliconValley).title("siliconValley"))
            it.addMarker(MarkerOptions().position(goldenGateBridge).title("goldenGateBridge"))
            it.addMarker(MarkerOptions().position(Alcatraz).title("Alcatraz"))
            it.addMarker(
                MarkerOptions().position(californiaAcademyOfSciences)
                    .title("californiaAcademyOfSciences")
            )

        }

        binding.button3.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, MainFragment())
                ?.commit()
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        fusedClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}