package com.prvavaja.grocerease.lists

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.AddEditItemActivity
import com.prvavaja.grocerease.MyApplication
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.model.BackendOperations
import com.prvavaja.grocerease.model.Serialization

class MyAdapterItems(val app: MyApplication) : RecyclerView.Adapter<MyAdapterItems.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTV: TextView = view.findViewById(R.id.itemNameTV)
        val quantityTV: TextView = view.findViewById(R.id.quantitySetText)
        val noteTV: TextView = view.findViewById(R.id.noteAddingTextTV)
        val deleteTV: ImageButton = view.findViewById(R.id.deleteButton)
        val editTV: ImageButton = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = app.currentList.items[position]
        holder.itemNameTV.text = current.item.name
        holder.quantityTV.text = "Quantity: ${current.quantity}"
        holder.noteTV.text = if (current.note.isNullOrEmpty()) {
            "Note: No note"
        } else {
            "Note: ${current.note}"
        }

        holder.editTV.setOnClickListener {
            Log.d("MyAdapterItems", "Edit button clicked for item: ${current.item.name}")
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_note, null)
            val noteEditText = dialogView.findViewById<EditText>(R.id.editNoteEditText)
            noteEditText.setText(current.note)

            AlertDialog.Builder(context)
                .setTitle("Edit Note")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newNote = noteEditText.text.toString()
                    val listId = app.currentList.id
                    val itemId = current.item.id ?: return@setPositiveButton

                    if (listId != null) {
                        BackendOperations().updateItemInList(
                            listId,
                            itemId,
                            quantity = current.quantity,
                            callback = { success, error ->
                                if (success) {
                                    current.note = newNote
                                    notifyItemChanged(position)
                                    Log.d("MyAdapterItems", "Note updated successfully")
                                } else {
                                    Log.e("MyAdapterItems", "Error updating note: $error")
                                }
                            }
                        )
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.deleteTV.setOnClickListener {
            Log.d("MyAdapterItems", "Delete button clicked for item: ${current.item.name}")
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item from your list?")
                .setPositiveButton("Yes") { _, _ ->
                    val listId = app.currentList.id
                    val itemId = current.item.id ?: return@setPositiveButton

                    if (listId != null) {
                        BackendOperations().deleteItemFromList(
                            listId,
                            itemId,
                            callback = { success, error ->
                                if (success) {
                                    app.currentList.items.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, app.currentList.items.size)
                                    Log.d("MyAdapterItems", "Item deleted successfully")
                                } else {
                                    Log.e("MyAdapterItems", "Error deleting item: $error")
                                }
                            }
                        )
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.itemView.setOnLongClickListener {
            Log.d("MyAdapterItems", "Long press on item: ${current.item.name}")
            true
        }

        // Optional: Regular click
        holder.itemView.setOnClickListener {
            openEditActivity(holder.itemView.context, position)
        }
    }

    private fun openEditActivity(context: Context, position: Int) {
        val intent = Intent(context, AddEditItemActivity::class.java)
        app.currentItem = app.currentList.items[position]
        context.startActivity(intent)
    }

    override fun getItemCount() = app.currentList.items.size
}
