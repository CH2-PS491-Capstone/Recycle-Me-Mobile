package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.models.RandomArticlesItem
import com.bangkit.recycleme.models.RekomendasiArticle
import com.bumptech.glide.Glide

class RekomendasiAdapter(private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<RekomendasiAdapter.UserViewHolder>() {

    private val storyList: MutableList<RandomArticlesItem> = mutableListOf()

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)
        private val recyclingImageView: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        fun bind(recycling: RandomArticlesItem) {

            descriptionTextView.text = recycling.alatBahan.toString()
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
            .inflate(R.layout.item_row_phone, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val story = storyList[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    fun setStories(stories: List<RandomArticlesItem>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    fun getStory(position: Int): RandomArticlesItem {
        return storyList[position]
    }
}