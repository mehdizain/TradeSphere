package com.example.tradesphere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.fragments.Message

class MessageAdapter(private var messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.tvSender.text = message.sender
        holder.tvMessage.text = message.message
        holder.tvTimestamp.text = message.timestamp.toString() // You can format this as needed
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    // Method to update messages when new data is fetched
    fun updateMessages(newMessages: List<Message>) {
        messageList = newMessages
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSender: TextView = itemView.findViewById(R.id.tvSender)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }
}
