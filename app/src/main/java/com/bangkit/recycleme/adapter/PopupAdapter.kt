package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R

class PopupAdapter(private val itemList: List<String>) : RecyclerView.Adapter<PopupAdapter.PopupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popup, parent, false)
        return PopupViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopupViewHolder, position: Int) {
        val itemText = itemList[position]
        holder.textItem.text = itemText
    }

    override fun getItemCount(): Int = itemList.size

    class PopupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textItem: TextView = itemView.findViewById(R.id.textItem)
    }
}
