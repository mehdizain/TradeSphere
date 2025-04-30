package com.example.tradesphere.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.adapters.MessageAdapter

class FragmentChat : Fragment() {

    // Declare views
    private lateinit var tvAppTitle: TextView
    private lateinit var tvChatHeader: TextView
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var btnBack: ImageButton

    private lateinit var chatTitle: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        // Initialize views
        tvAppTitle = rootView.findViewById(R.id.tvAppTitle)
        tvChatHeader = rootView.findViewById(R.id.tvChatHeader)
        recyclerMessages = rootView.findViewById(R.id.recyclerMessages)
        etMessageInput = rootView.findViewById(R.id.etMessageInput)
        btnSendMessage = rootView.findViewById(R.id.btnSendMessage)
        btnBack = rootView.findViewById(R.id.btnBack)

        // Set the app title
        tvAppTitle.text = "TradeSphere"

        // Get the chat title passed from the previous fragment (e.g., "Shoes Palace")
        chatTitle = arguments?.getString("CHAT_TITLE") ?: "Unknown Chat"
        tvChatHeader.text = chatTitle

        // Set up RecyclerView for chat messages
        recyclerMessages.layoutManager = LinearLayoutManager(context)
        recyclerMessages.adapter = MessageAdapter(emptyList()) // Replace with actual message data

        // Handle back button click
        btnBack.setOnClickListener {
            // Go back to the previous fragment
            activity?.supportFragmentManager?.popBackStack()
        }

        // Handle send message button click
        btnSendMessage.setOnClickListener {
            val message = etMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                // Here you would send the message to the chat
                // You can add the message to the RecyclerView or send it to the backend
                etMessageInput.text.clear()
            }
        }

        return rootView
    }
}
