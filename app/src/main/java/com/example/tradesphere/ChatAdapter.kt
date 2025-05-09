package com.example.tradesphere

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R

class ChatAdapter(
    private val chats: List<ChatItem>,
    private val currentUserId: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.tvUsername.text = chat.username
        holder.tvLastMessage.text = if (chat.lastMessage.isNotEmpty()) chat.lastMessage else "No messages yet"
        // Placeholder avatar (update with real profile image if available)
        holder.ivProfile.setImageResource(R.drawable.sample_product)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java).apply {
                putExtra("currentUserId", currentUserId)
                putExtra("receiverId", chat.userId)
                putExtra("receiverUsername", chat.username)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = chats.size
}