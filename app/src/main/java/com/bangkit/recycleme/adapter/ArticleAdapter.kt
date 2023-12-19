package com.bangkit.recycleme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.databinding.ItemRowPhoneBinding
import com.bangkit.recycleme.models.ListArticlesItem
import com.bumptech.glide.Glide

class ArticleAdapter(private val onClickListener: View.OnClickListener) :
    PagingDataAdapter<ListArticlesItem, ArticleAdapter.UserViewHolder>(DIFF_CALLBACK) {

    inner class UserViewHolder(private val binding: ItemRowPhoneBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListArticlesItem) {
            val maxWords = 50
            val fullDescription = story.alatBahan

            val words = fullDescription?.split(" ")

            val limitedDescription = if (words?.size!! > maxWords) {
                words.subList(0, maxWords).joinToString(" ") + "..."
            } else {
                fullDescription
            }

            binding.tvItemDescription.text = limitedDescription
            binding.tvItemName.text = story.judul

            Glide.with(itemView.context)
                .load(story.gambar)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onClickListener.onClick(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemRowPhoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }


    fun getArticle(position: Int): ListArticlesItem? {
        return getItem(position)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListArticlesItem>() {
            override fun areItemsTheSame(oldItem: ListArticlesItem, newItem: ListArticlesItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListArticlesItem, newItem: ListArticlesItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}