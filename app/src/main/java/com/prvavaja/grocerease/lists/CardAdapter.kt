package com.prvavaja.grocerease.lists

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

        Picasso.get()
            .load(card.imgUrl)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_not_supported_24)
            .into(holder.cardImageView)
    }

    override fun getItemCount() = cardList.size
}
