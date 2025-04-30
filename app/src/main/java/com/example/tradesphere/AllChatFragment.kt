package com.example.tradesphere

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.view.View

class AllChatsFragment : Fragment() {

    // Data class for chat
    data class Chat(val username: String, val lastMessage: String)

    // Adapter for RecyclerView
    class ChatsAdapter(private val chats: List<Chat>) :
        RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

        inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
            val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_chat, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val chat = chats[position]
            holder.tvUsername.text = chat.username
            holder.tvLastMessage.text = chat.lastMessage
        }

        override fun getItemCount(): Int = chats.size
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatsAdapter
    private val chatList = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_allchats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerChats)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data
        chatList.add(Chat("John Doe", "Hey, I'm interested in your product!"))
        chatList.add(Chat("Alice Smith", "Is the item still available?"))
        chatList.add(Chat("Bob Johnson", "Thanks for the quick response!"))

        adapter = ChatsAdapter(chatList)
        recyclerView.adapter = adapter
    }
}
