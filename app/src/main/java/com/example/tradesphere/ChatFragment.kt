package com.example.tradesphere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SingleChatActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var tvChatHeader: TextView
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessageInput: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var btnBack: ImageButton

    // Example list of messages for the chat
    private val messageList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat)

        // Initialize views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        tvChatHeader = findViewById(R.id.tvChatHeader)
        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessageInput = findViewById(R.id.etMessageInput)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        btnBack = findViewById(R.id.btnBack)

        // Set the chat header title (can be dynamic based on chat)
        tvChatHeader.text = "Shoes Palace" // Change dynamically as needed

        // Setup RecyclerView
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        val chatAdapter = ChatAdapter(messageList) // Custom adapter to display messages
        recyclerMessages.adapter = chatAdapter

        // Handle send message button click
        btnSendMessage.setOnClickListener {
            val message = etMessageInput.text.toString()
            if (message.isNotEmpty()) {
                // Add message to the list and update RecyclerView
                messageList.add(message)
                chatAdapter.notifyItemInserted(messageList.size - 1)
                recyclerMessages.scrollToPosition(messageList.size - 1) // Scroll to bottom
                etMessageInput.text.clear() // Clear input field
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        btnBack.setOnClickListener {
            onBackPressed() // Navigate back to the previous screen
        }
    }

    // ChatAdapter class
    class ChatAdapter(private val messages: List<String>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_chat, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            holder.bind(messages[position])
        }

        override fun getItemCount(): Int = messages.size

        inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)

            fun bind(message: String) {
                tvMessage.text = message
            }
        }
    }
}
