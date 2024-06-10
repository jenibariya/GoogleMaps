package com.example.myapplication.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.roomdatabase.SearchLocation
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.launch

class SearchLocationViewModel(private val repository: SearchLocationRepository) : ViewModel() {

    private val _filterLocation = MutableLiveData<List<SearchLocation>>()
    val filterLocations: LiveData<List<SearchLocation>> get() = _filterLocation

    private val _selectedPlace = MutableLiveData<Place?>()
    val selectedPlace: MutableLiveData<Place?>
        get() = _selectedPlace

    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
    }

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _filterLocation.value = repository.getAllSearchLocations()
        }
    }

    fun saveSearchLocationToDatabase(searchLocation: SearchLocation) {
        viewModelScope.launch {
            repository.saveSearchLocation(searchLocation)
            fetchData() // Refresh data after saving
        }
    }

    fun deleteSearchLocation(searchLocation: SearchLocation) {
        viewModelScope.launch {
            repository.deleteSearchLocation(searchLocation)
            fetchData() // Refresh data after deleting
        }
    }

    fun updateSearchLocation(searchLocation: SearchLocation) {
        viewModelScope.launch {
            repository.updateSearchLocation(searchLocation)
            fetchData() // Refresh data after updating
        }
    }

    fun sortLocationsAscending() {
        val sortedList = _filterLocation.value?.sortedBy { it.name }
        _filterLocation.value = sortedList!!
    }

    fun sortLocationsDescending() {
        val sortedList = _filterLocation.value?.sortedByDescending { it.name }
        _filterLocation.value = sortedList!!
    }
}




