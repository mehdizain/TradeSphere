package com.example.tradesphere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var tvReceiverUsername: TextView
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button

    private lateinit var currentUserId: String
    private lateinit var receiverId: String
    private lateinit var receiverUsername: String

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val messages = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_chat)

        // Initialize views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        tvReceiverUsername = findViewById(R.id.tvReceiverUsername)
        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Get Intent data
        currentUserId = intent.getStringExtra("currentUserId") ?: ""
        receiverId = intent.getStringExtra("receiverId") ?: ""
        receiverUsername = intent.getStringExtra("receiverUsername") ?: "Unknown"

        if (currentUserId.isEmpty() || receiverId.isEmpty()) {
            Toast.makeText(this, "Invalid chat users", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up UI
        tvReceiverUsername.text = receiverUsername
        recyclerMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Scroll to bottom
        }
        messageAdapter = MessageAdapter(messages, currentUserId)
        recyclerMessages.adapter = messageAdapter

        // Make receiver username clickable
        tvReceiverUsername.setOnClickListener {
            if (receiverUsername.isNotEmpty() && receiverUsername != "Unknown") {
                val intent = Intent(this, UserProfileActivity::class.java).apply {
                    putExtra("username", receiverUsername)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid user profile", Toast.LENGTH_SHORT).show()
            }
        }

        // Load messages
        listenForMessages()

        // Send message
        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                etMessage.text.clear()
            } else {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(text: String) {
        val message = hashMapOf(
            "senderId" to currentUserId,
            "receiverId" to receiverId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("messages")
            .add(message)
            .addOnSuccessListener {
                // Message sent, RecyclerView will update via listener
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenForMessages() {
        firestore.collection("messages")
            .whereIn("senderId", listOf(currentUserId, receiverId))
            .whereIn("receiverId", listOf(currentUserId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val senderId = doc.getString("senderId") ?: continue
                        val receiverId = doc.getString("receiverId") ?: continue
                        val text = doc.getString("text") ?: continue
                        val timestamp = doc.getLong("timestamp") ?: 0
                        messages.add(Message(senderId, receiverId, text, timestamp))
                    }
                    messageAdapter.notifyDataSetChanged()
                    if (messages.isNotEmpty()) {
                        recyclerMessages.scrollToPosition(messages.size - 1)
                    }
                }
            }
    }
}