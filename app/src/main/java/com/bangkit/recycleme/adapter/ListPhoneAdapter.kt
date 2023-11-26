package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.models.Phone
import com.bangkit.recycleme.R

class ListPhoneAdapter(private val listPhone: ArrayList<Phone>) : RecyclerView.Adapter<ListPhoneAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_phone, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listPhone.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // detailDescription hanya untuk syarat penuhi properti dataClass Phone, tapi tidak dipanggil
        val (name, description, detailDescription, photo, price) = listPhone[position]
        holder.imgPhoto.setImageResource(photo)
        holder.tvName.text = name
        holder.tvDescription.text = description
        holder.tvPrice.text = price
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listPhone[holder.adapterPosition]) }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Phone)
    }

}