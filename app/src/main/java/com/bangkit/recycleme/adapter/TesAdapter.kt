package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.models.ArticlesItem
import com.bangkit.recycleme.models.ListArticlesItem
import com.bumptech.glide.Glide

class TesAdapter(private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<TesAdapter.UserViewHolder>() {

    private val storyList: MutableList<ArticlesItem> = mutableListOf()

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)
        private val recyclingImageView: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        fun bind(recycling: ArticlesItem) {
            val maxWords = 50
            val fullDescription = recycling.alatBahan

            val words = fullDescription?.split(" ")

            val limitedDescription = if (words?.size!! > maxWords) {
                words.subList(0, maxWords).joinToString(" ") + "..."
            } else {
                fullDescription
            }

            descriptionTextView.text = limitedDescription
            tvName.text = recycling.judul

            Glide.with(itemView.context)
                .load(recycling.gambar)
                .into(recyclingImageView)

            itemView.setOnClickListener {
                onClickListener.onClick(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_favorite, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val story = storyList[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    fun setStories(stories: List<ArticlesItem>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    fun getStory(position: Int): ArticlesItem {
        return storyList[position]
    }
}