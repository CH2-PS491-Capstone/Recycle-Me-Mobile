package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ItemRowPhoneBinding
import com.bangkit.recycleme.models.ListArticlesItem
import com.bangkit.recycleme.models.ListRecyclingItem
import com.bumptech.glide.Glide

class ArticleAdapter(private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<ArticleAdapter.UserViewHolder>() {

    private val storyList: MutableList<ListArticlesItem> = mutableListOf()

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_item_description)
        private val recyclingImageView: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        fun bind(recycling: ListArticlesItem) {
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

    fun setStories(stories: List<ListArticlesItem>) {
        storyList.clear()
        storyList.addAll(stories)
        notifyDataSetChanged()
    }

    fun getStory(position: Int): ListArticlesItem {
        return storyList[position]
    }
}