package com.example.tradesphere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.fragments.Post
import com.bumptech.glide.Glide

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        val postCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val postImage: ImageView = itemView.findViewById(R.id.imgPostImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postTitle.text = post.text
        holder.postCategory.text = post.category

        // If there's an image URL, load it using Glide
        post.imageUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.postImage)
        } ?: run {
            // If no image URL, you can hide the ImageView
            holder.postImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
