package com.example.tradesphere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R

class ChatAdapter(private val chatList: List<String>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatName = chatList[position]
        holder.tvChatName.text = chatName
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChatName: TextView = itemView.findViewById(R.id.tvChatName)
    }
}
