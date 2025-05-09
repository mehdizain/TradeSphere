package com.example.tradesphere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.fragments.Post

class PostAdapter(
    private var posts: List<Post>,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
        holder.itemView.setOnClickListener { onPostClick(post) }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvPostContent)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvPostCategory)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivPostImage)

        fun bind(post: Post) {
            tvTitle.text = post.title
            tvContent.text = post.text
            tvCategory.text = post.category
            if (post.imageUrl != null) {
                ivImage.setImageURI(android.net.Uri.parse(post.imageUrl))
                ivImage.visibility = View.VISIBLE
            } else {
                ivImage.setImageResource(R.drawable.ic_default_profile)
                ivImage.visibility = View.GONE
            }
        }
    }
}