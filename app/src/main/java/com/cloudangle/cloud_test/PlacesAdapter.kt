package com.cloudangle.cloud_test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlacesAdapter(private val places: List<Place>) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.txt_title_distance)
        val addressTextView: TextView = itemView.findViewById(R.id.txt_title_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.nameTextView.text = place.name
        holder.addressTextView.text = place.address
        holder.itemView.setOnClickListener {
            val intent: Intent = Intent(context, GoogleMapActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }
    companion object{
        var context:Context? = null
    }
}
