package com.prvavaja.grocerease.lists

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prvavaja.grocerease.R
import com.prvavaja.grocerease.model.Card
import com.squareup.picasso.Picasso // For loading images

class CardAdapter(private val cardList: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val barcodeText: TextView = view.findViewById(R.id.textViewBarcode)
        val shopNameText: TextView = view.findViewById(R.id.textViewShopName)
        val cardImageView: ImageView = view.findViewById(R.id.imageViewCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.barcodeText.text = card.barcodeNum
        holder.shopNameText.text = card.shopName
        val base64Image = card.imgUrl
        if (base64Image != null) {
            try {
                // Decode Base64 string into a byte array
                val imageBytes = Base64.decode(base64Image.split(",").last(), Base64.DEFAULT)
                // Convert byte array into a Bitmap
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                // Set the Bitmap to the ImageView
                holder.cardImageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                // Set a placeholder image in case of an error
                holder.cardImageView.setImageResource(R.drawable.baseline_image_not_supported_24)
            }
        } else {
            // Set a placeholder image if no Base64 string is provided
            holder.cardImageView.setImageResource(R.drawable.baseline_image_24)
        }
    }

    override fun getItemCount() = cardList.size
}
