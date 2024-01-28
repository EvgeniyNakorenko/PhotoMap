package com.example.m19_location.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m19_location.data.PhotoDao
import com.example.m19_location.data.SinglePhoto
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val photoDao: PhotoDao) : ViewModel() {

    private var allPhotos = this.photoDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    val resultState = allPhotos

    fun saveSinglePhoto(photo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            photoDao.insert(
                singlePhoto = SinglePhoto(
                    savedUri = photo
                )
            )
        }
    }

    companion object{
         val siliconValley = LatLng(37.3775, -122.0675)
         val goldenGateBridge = LatLng(37.8175, -122.478333)
         val Alcatraz = LatLng(37.826667, -122.422778)
         val californiaAcademyOfSciences = LatLng(37.77, -122.466389)
    }

}