package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.SearchLocationAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.roomdatabase.AppDatabase
import com.example.myapplication.util.PrefData
import com.example.myapplication.mvvm.SearchLocationRepository
import com.example.myapplication.mvvm.SearchLocationViewModel
import com.example.myapplication.mvvm.SearchLocationViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: SearchLocationAdapter

    private lateinit var searchLocationViewModel: SearchLocationViewModel

    lateinit var myPrefs: PrefData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val database = AppDatabase.getDatabase(applicationContext)
        val videosDao = database.videosDao()
        val repository = SearchLocationRepository(videosDao)

        searchLocationViewModel =
            ViewModelProvider(this, SearchLocationViewModelFactory(repository)).get(
                SearchLocationViewModel::class.java
            )

        myPrefs = PrefData(this)

        binding.addLocation.setOnClickListener {
            myPrefs.saveExampleBoolean(false)
            val i = Intent(this, SearchLocationActivity::class.java)
            startActivity(i)
        }

        setupRecyclerView()
        observeViewModel()

        val selectedRadioButtonId = myPrefs.getSelectedRadioButtonId()
        if (selectedRadioButtonId != -1) {
            binding.groupRadio.check(selectedRadioButtonId)
        }

        binding.locationRoute.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            val locations = searchLocationViewModel.filterLocations.value ?: emptyList()
            intent.putParcelableArrayListExtra("locations", ArrayList(locations))
            startActivity(intent)
        }
        binding.locationFilterIV.setOnClickListener {
            binding.filterLayout.visibility = View.VISIBLE

            binding.groupRadio.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radioButton = group
                        .findViewById<View>(checkedId) as RadioButton
                    myPrefs.saveSelectedRadioButtonId(checkedId)
                })

            binding.applyButton.setOnClickListener(View.OnClickListener {
                val selectedId: Int = binding.groupRadio.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(
                        this@MainActivity,
                        "No answer has been selected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val radioButton = binding.groupRadio
                        .findViewById(selectedId) as RadioButton

                    binding.sortLocationsDescendingRB.setOnClickListener {
                        searchLocationViewModel.sortLocationsAscending()

                    }
                    binding.sortLocationsAscendingRB.setOnClickListener {
                        searchLocationViewModel.sortLocationsDescending()
                    }

                    binding.filterLayout.visibility = View.GONE
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        searchLocationViewModel.fetchData()
    }

    private fun setupRecyclerView() {
        binding.locationList.layoutManager = LinearLayoutManager(this)
        adapter = SearchLocationAdapter(
            mutableListOf(),
            onUpdateClick = { location ->
                myPrefs.saveExampleBoolean(true)

                val intent = Intent(this, SearchLocationActivity::class.java).apply {
                    putExtra("id", location.id)
                    putExtra("name", location.name)
                    putExtra("address", location.address)
                    putExtra("latitude", location.latitude)
                    putExtra("longitude", location.longitude)
                }
                startActivity(intent)
            },
            onDeleteClick = { location ->
                searchLocationViewModel.deleteSearchLocation(location)
            }, this
        )
        binding.locationList.adapter = adapter

    }

    private fun observeViewModel() {
        searchLocationViewModel.filterLocations.observe(this, Observer { locations ->
            if (locations.isEmpty()) {
                binding.addPlaceTV.visibility = View.VISIBLE
                binding.locationList.visibility = View.GONE
            } else {
                binding.addPlaceTV.visibility = View.GONE
                binding.locationList.visibility = View.VISIBLE
                adapter.updateLocations(locations)
            }
        })
    }
}