package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.models.ListRecyclingItem
import com.bumptech.glide.Glide

class RecyclingAdapter(private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<RecyclingAdapter.UserViewHolder>() {

    private val storyList: MutableList<ListRecyclingItem> = mutableListOf()


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.nameRecycling)
        private val recyclingImageView: ImageView = itemView.findViewById(R.id.imageRecycling)

        fun bind(recycling: ListRecyclingItem) {
            val maxWords = 50
            val fullDescription = recycling.recycling

            val words = fullDescription?.split(" ")

            val limitedDescription = if (words?.size!! > maxWords) {
                words.subList(0, maxWords).joinToString(" ") + "..."
            } else {
                fullDescription
            }

            descriptionTextView.text = limitedDescription

            Glide.with(itemView.context)
                .load(recycling.photoUrl)
                .into(recyclingImageView)

            itemView.setOnClickListener {
                onClickListener.onClick(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_recycling, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val story = storyList[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    fun setStories(stories: List<ListRecyclingItem>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    fun getStory(position: Int): ListRecyclingItem {
        return storyList[position]
    }
}