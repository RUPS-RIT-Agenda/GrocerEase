package com.prvavaja.grocerease.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.model.ItemInList

class ItemsInListAdapter(
    private val items: List<ItemInList>,
    private val onAddClick: (ItemInList) -> Unit
) : RecyclerView.Adapter<ItemsInListAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTV: TextView = view.findViewById(R.id.itemNameTv)
        val quantityTV: TextView = view.findViewById(R.id.quantitySetText)
        val noteTV: TextView = view.findViewById(R.id.noteAddingTextTV)
        val boughtCB: CheckBox = view.findViewById(R.id.boughtCB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItemInList = items[position]

        holder.itemNameTV.text = currentItemInList.item.name

        holder.quantityTV.text = "Quantity: ${currentItemInList.quantity}"

        holder.noteTV.text = if (currentItemInList.note.isNullOrEmpty()) {
            "Note: No note"
        } else {
            "Note: ${currentItemInList.note}"
        }

        holder.boughtCB.isChecked = currentItemInList.bought

        holder.boughtCB.setOnCheckedChangeListener { _, isChecked ->
            currentItemInList.bought = isChecked
        }
    }


    override fun getItemCount(): Int = items.size
}
