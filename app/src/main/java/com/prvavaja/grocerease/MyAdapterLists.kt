package com.prvavaja.grocerease

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyAdapterLists(val app: MyApplication) :
    RecyclerView.Adapter<MyAdapterLists.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listNameTV: TextView
        val numOfItemsTV: TextView
        val createdTV: TextView
        init{
            listNameTV = itemView.findViewById(R.id.listNameTV)
            numOfItemsTV = itemView.findViewById(R.id.numOfItemsTV)
            createdTV = itemView.findViewById(R.id.createdTV)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = app.listOfgrocerylists.getAllLists()[position]
        holder.listNameTV.text = current.listName
        holder.numOfItemsTV.text = current.items.size.toString()
        holder.createdTV.text = current.date

        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, position)
            true
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
        if(app.listOfgrocerylists.getAllLists()[position].items.size >= 1){
            Toast.makeText(context, "You can't delete shopping lists that have items in it", Toast.LENGTH_SHORT).show()
            return
        }
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Item")
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val serialization = Serialization(context)
            serialization.delete(app.listOfgrocerylists.getAllLists()[position].uuid)
            app.listOfgrocerylists.getAllLists().removeAt(position)

            notifyItemRemoved(position)
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun getItemCount() = app.listOfgrocerylists.getAllLists().size
}