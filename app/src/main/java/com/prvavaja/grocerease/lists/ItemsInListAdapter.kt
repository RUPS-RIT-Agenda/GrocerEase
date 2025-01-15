package com.prvavaja.grocerease.lists

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.model.ItemInList

class ItemsInListAdapter(
    private val items: MutableList<ItemInList>,
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

        holder.itemView.findViewById<ImageButton>(R.id.editButton).setOnClickListener {
            Log.d("ItemsInListAdapter", "Edit button clicked for item: ${currentItemInList.item.name}")
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_note, null)
            val noteEditText = dialogView.findViewById<EditText>(R.id.editNoteEditText)
            noteEditText.setText(currentItemInList.note)

            AlertDialog.Builder(context)
                .setTitle("Edit Note")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newNote = noteEditText.text.toString()
                    currentItemInList.note = newNote
                    notifyItemChanged(position)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.itemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            Log.d("ItemsInListAdapter", "Delete button clicked for item: ${currentItemInList.item.name}")
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item from your list?")
                .setPositiveButton("Yes") { _, _ ->
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, items.size)
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    override fun getItemCount(): Int = items.size
}
