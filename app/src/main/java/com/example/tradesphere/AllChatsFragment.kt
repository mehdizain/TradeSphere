package com.example.tradesphere.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.adapters.ChatAdapter

class AllChatsFragment : Fragment() {

    // Declare views
    private lateinit var tvAppTitle: TextView
    private lateinit var tvChatsHeader: TextView
    private lateinit var recyclerChats: RecyclerView
    private lateinit var imgLogo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_allchats, container, false)

        // Initialize views
        tvAppTitle = rootView.findViewById(R.id.tvAppTitle)
        tvChatsHeader = rootView.findViewById(R.id.tvChatsHeader)
        recyclerChats = rootView.findViewById(R.id.recyclerChats)
        imgLogo = rootView.findViewById(R.id.imgLogo)

        // Set the app title
        tvAppTitle.text = "TradeSphere"

        // Set up RecyclerView for chats
        recyclerChats.layoutManager = LinearLayoutManager(context)

        // Sample data for chats (replace with real data)
        val chatList = listOf(
            "Chat 1", "Chat 2", "Chat 3", "Chat 4", "Chat 5" // Replace with actual data
        )

        // Set adapter
        recyclerChats.adapter = ChatAdapter(chatList)

        return rootView
    }
}
