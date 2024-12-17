package com.prvavaja.grocerease.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.model.Item

class ItemsAdapter(
    private val items: List<Item>,
    private val onAddClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTV: TextView = view.findViewById(R.id.itemNameTV)
        val itemDescriptionTV: TextView = view.findViewById(R.id.storeNameTV)
        val addItemIV: ImageView = view.findViewById(R.id.addItemIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemNameTV.text = currentItem.name
        holder.itemDescriptionTV.text = currentItem.description

        holder.addItemIV.setOnClickListener {
            onAddClick(currentItem)
        }
    }

    override fun getItemCount(): Int = items.size
}
