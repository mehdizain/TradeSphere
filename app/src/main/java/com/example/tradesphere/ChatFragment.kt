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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth

class FragmentChat : Fragment() {

    // Declare views
    private lateinit var tvAppTitle: TextView
    private lateinit var tvChatHeader: TextView
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var btnBack: ImageButton

    private lateinit var chatTitle: String
    private lateinit var chatId: String

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Firebase Authentication instance
    private val auth = FirebaseAuth.getInstance()

    // Adapter for messages
    private lateinit var messageAdapter: MessageAdapter

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
        chatId = arguments?.getString("CHAT_ID") ?: "unknown_chat_id"
        tvChatHeader.text = chatTitle

        // Set up RecyclerView for chat messages
        recyclerMessages.layoutManager = LinearLayoutManager(context)
        messageAdapter = MessageAdapter(emptyList())
        recyclerMessages.adapter = messageAdapter

        // Fetch messages from Firestore
        fetchMessages()

        // Handle back button click
        btnBack.setOnClickListener {
            // Go back to the previous fragment
            activity?.supportFragmentManager?.popBackStack()
        }

        // Handle send message button click
        btnSendMessage.setOnClickListener {
            val message = etMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessageInput.text.clear() // Clear input after sending message
            }
        }

        return rootView
    }

    private fun fetchMessages() {
        // Fetch messages from Firestore for the current chat
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.map { document ->
                        val sender = document.getString("sender") ?: ""
                        val message = document.getString("message") ?: ""
                        val timestamp = document.getLong("timestamp") ?: 0L
                        Message(sender, message, timestamp)
                    }
                    messageAdapter.updateMessages(messages)
                }
            }
    }

    private fun sendMessage(message: String) {
        // Get the current user
        val user = auth.currentUser
        if (user != null) {
            // Create message object
            val messageData = hashMapOf(
                "sender" to user.displayName,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )

            // Send the message to Firestore
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener {
                    // Message sent successfully
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }
}
