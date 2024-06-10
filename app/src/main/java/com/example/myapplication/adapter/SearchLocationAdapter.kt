package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemSearchLocationBinding
import com.example.myapplication.roomdatabase.SearchLocation

class SearchLocationAdapter(
    private var locations: MutableList<SearchLocation>,
    private val onUpdateClick: (SearchLocation) -> Unit,
    private val onDeleteClick: (SearchLocation) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<SearchLocationAdapter.SearchLocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        val binding =
            ItemSearchLocationBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchLocationViewHolder(binding)

    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int {
        return locations.size
    }


    fun updateLocations(newLocations: List<SearchLocation>) {
        locations.clear()
        locations.addAll(newLocations)
        notifyDataSetChanged()
    }

    inner class SearchLocationViewHolder(private val binding: ItemSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: SearchLocation) {
            binding.locationNameTextView.text = location.name
            binding.locationAddressTextView.text = location.address

            binding.updateButton.setOnClickListener { onUpdateClick(location) }
            binding.deleteButton.setOnClickListener {
                onDeleteClick(location)
                locations.remove(location)
                notifyDataSetChanged()
            }

        }
    }
}
