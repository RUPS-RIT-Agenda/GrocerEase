package com.prvavaja.grocerease

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private val stores: List<String>) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    class StoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeNameTextView: TextView = view.findViewById(R.id.listNameTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_favourite, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.storeNameTextView.text = stores[position]
    }

    override fun getItemCount() = stores.size
}