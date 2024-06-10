package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySearchLocationBinding
import com.example.myapplication.roomdatabase.AppDatabase
import com.example.myapplication.roomdatabase.SearchLocation
import com.example.myapplication.util.PrefData
import com.example.myapplication.mvvm.SearchLocationRepository
import com.example.myapplication.mvvm.SearchLocationViewModel
import com.example.myapplication.mvvm.SearchLocationViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class SearchLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchLocationBinding

    private lateinit var mMap: GoogleMap
    private var previousMarker: Marker? = null

    private lateinit var viewModel: SearchLocationViewModel

    lateinit var myPrefs: PrefData

    lateinit var mapFragment: SupportMapFragment

    private var currentLocationId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(applicationContext)
        val videosDao = database.videosDao()
        val repository = SearchLocationRepository(videosDao)

        viewModel = ViewModelProvider(this, SearchLocationViewModelFactory(repository)).get(
            SearchLocationViewModel::class.java
        )

        myPrefs = PrefData(this)

        mapFragment = supportFragmentManager.findFragmentById(R.id.frag) as SupportMapFragment

        currentLocationId = intent.getIntExtra("id", 0)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        viewModel.selectedPlace.observe(this) { place ->
            place?.let {
                onPlaceSelected(place)
            }
        }

        val autocompleteSupportFragment1 =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?

        autocompleteSupportFragment1!!.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL

            )
        )

        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                handlePlaceSelected(place)
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            val name = intent.getStringExtra("name")
            val address = intent.getStringExtra("address")
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)

            if (latitude != 0.0 && longitude != 0.0) {
                val latLng = LatLng(latitude, longitude)
                val newMarker = mMap.addMarker(MarkerOptions().position(latLng).title(name))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                previousMarker = newMarker

                viewModel.setSelectedPlace(
                    Place.builder().setName(name).setAddress(address)
                        .setLatLng(LatLng(latitude, longitude)).build()
                )

            }

            val locations: ArrayList<SearchLocation> =
                intent.getParcelableArrayListExtra("locations") ?: arrayListOf()
            if (locations.isNotEmpty()) {
                binding.ll1.visibility = View.GONE
                addMarkersAndPath(locations)
            }
        }

    }

    private fun addMarkersAndPath(locations: List<SearchLocation>) {
        mMap.clear()
        val latLngList = mutableListOf<LatLng>()

        for (location in locations) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location.name))
            latLngList.add(latLng)
        }

        if (latLngList.size > 1) {
            mMap.addPolyline(PolylineOptions().addAll(latLngList).color(Color.BLUE).width(5f))
        }

        if (latLngList.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            for (latLng in latLngList) {
                boundsBuilder.include(latLng)
            }
            val bounds = boundsBuilder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun handlePlaceSelected(place: Place) {
        previousMarker?.remove()

        val name = place.name
        val address = place.address
        val latLng = place.latLng

        if (latLng != null) {
            val newMarker = mMap.addMarker(MarkerOptions().position(latLng).title(name))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            previousMarker = newMarker
        }

        viewModel.setSelectedPlace(place)

        binding.saveLayout.visibility = View.VISIBLE

        if (myPrefs.getExampleBoolean()) {
            binding.saveLocation.text = "Update"
            binding.savePlaceTV.text = getString(R.string.update_place)
        } else {
            binding.saveLocation.text = "Save"
        }

        binding.saveLocation.setOnClickListener {
            val searchLocation = SearchLocation(
                id = currentLocationId ?: 0,
                name = name,
                address = address,
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )

            if (myPrefs.getExampleBoolean()) {
                viewModel.updateSearchLocation(searchLocation)

                Log.e("TAG", "onPlaceSelected: $searchLocation")
            } else {
                viewModel.saveSearchLocationToDatabase(searchLocation)
            }

            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
        }
    }


    private fun onPlaceSelected(place: Place) {
        previousMarker?.remove()

        val name = place.name
        val address = place.address
        val latLng = place.latLng

        if (latLng != null) {
            val newMarker = mMap.addMarker(MarkerOptions().position(latLng).title(name))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            previousMarker = newMarker
        }
    }
}