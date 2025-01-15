package com.prvavaja.grocerease.lists

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.MyApplication
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.SingleListActivity
import com.prvavaja.grocerease.model.Serialization
import java.time.format.DateTimeFormatter

class MyAdapterLists(val app: MyApplication) :
    RecyclerView.Adapter<MyAdapterLists.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNameTV: TextView = itemView.findViewById(R.id.listNameTV)
        val storeNameTV: TextView = itemView.findViewById(R.id.storeNameTV)
        val numOfItemsTV: TextView = itemView.findViewById(R.id.numOfItemsTV)
        val createdTV: TextView = itemView.findViewById(R.id.createdTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = app.listOfgrocerylists.getAllLists()[position]
        holder.listNameTV.text = current.listName
        holder.storeNameTV.text = "List for store: ${current.company}"
        holder.numOfItemsTV.text = current.items.size.toString()

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val formattedDate = try {
            val isoDate = java.time.OffsetDateTime.parse(current.date)
            isoDate.toLocalDate().format(dateFormatter)
        } catch (e: Exception) {
            current.date
        }
        holder.createdTV.text = formattedDate

        holder.itemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, position)
        }

        holder.itemView.setOnClickListener {
            showGroceryList(holder.itemView.context, position)
        }
    }


    private fun showGroceryList(context: Context, position: Int) {
        val intent = Intent(context, SingleListActivity::class.java)
        app.currentList = app.listOfgrocerylists.getAllLists()[position]
        context.startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        val currentList = app.listOfgrocerylists.getAllLists()[position]

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Shopping List")
        alertDialogBuilder.setMessage("Are you sure you want to delete the shopping list \"${currentList.listName}\"?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val serialization = Serialization(context)

            serialization.delete(currentList.id)

            app.listOfgrocerylists.getAllLists().removeAt(position)
            notifyItemRemoved(position)

            Toast.makeText(context, "Shopping list \"${currentList.listName}\" deleted.", Toast.LENGTH_SHORT).show()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun getItemCount() = app.listOfgrocerylists.getAllLists().size
}