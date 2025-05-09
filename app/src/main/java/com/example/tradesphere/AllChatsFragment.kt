package com.example.tradesphere.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.ChatItem
import com.example.tradesphere.ChatAdapter
import com.example.tradesphere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class AllChatsFragment : Fragment() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var recyclerChats: RecyclerView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val chats = mutableListOf<ChatItem>()
    private lateinit var chatAdapter: ChatAdapter
    private val TAG = "AllChatsFragment"
    private var senderListener: ListenerRegistration? = null
    private var receiverListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_allchats, container, false)

        // Initialize views
        imgLogo = rootView.findViewById(R.id.imgLogo)
        tvAppTitle = rootView.findViewById(R.id.tvAppTitle)
        recyclerChats = rootView.findViewById(R.id.recyclerChats)

        // Set up RecyclerView
        recyclerChats.layoutManager = LinearLayoutManager(context)
        val currentUserId = auth.currentUser?.uid ?: ""
        chatAdapter = ChatAdapter(chats, currentUserId)
        recyclerChats.adapter = chatAdapter

        // Fetch chats
        if (currentUserId.isNotEmpty()) {
            fetchChats(currentUserId)
        } else {
            Toast.makeText(context, "Please log in to view chats", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firestore listeners to prevent memory leaks
        senderListener?.remove()
        receiverListener?.remove()
    }

    private fun fetchChats(currentUserId: String) {
        val otherUserIds = mutableSetOf<String>()

        // Real-time listener for messages where current user is sender
        senderListener = firestore.collection("messages")
            .whereEqualTo("senderId", currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching sender messages: ${e.message}", e)
                    Toast.makeText(context, "Failed to load chats", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val receiverId = doc.getString("receiverId") ?: continue
                        otherUserIds.add(receiverId)
                    }
                    fetchUsernamesAndLastMessages(otherUserIds, currentUserId)
                }
            }

        // Real-time listener for messages where current user is receiver
        receiverListener = firestore.collection("messages")
            .whereEqualTo("receiverId", currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching receiver messages: ${e.message}", e)
                    Toast.makeText(context, "Failed to load chats", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val senderId = doc.getString("senderId") ?: continue
                        otherUserIds.add(senderId)
                    }
                    fetchUsernamesAndLastMessages(otherUserIds, currentUserId)
                }
            }
    }

    private fun fetchUsernamesAndLastMessages(otherUserIds: Set<String>, currentUserId: String) {
        chats.clear()
        if (otherUserIds.isEmpty()) {
            chatAdapter.notifyDataSetChanged()
            Toast.makeText(context, "No chats found", Toast.LENGTH_SHORT).show()
            return
        }

        for (userId in otherUserIds) {
            // Fetch username (one-time query)
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc != null && userDoc.exists()) {
                        val username = userDoc.getString("username") ?: "Unknown"

                        // Real-time listener for last message (sent by current user to userId)
                        val query1 = firestore.collection("messages")
                            .whereEqualTo("senderId", currentUserId)
                            .whereEqualTo("receiverId", userId)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(1)

                        // Real-time listener for last message (sent by userId to current user)
                        val query2 = firestore.collection("messages")
                            .whereEqualTo("senderId", userId)
                            .whereEqualTo("receiverId", currentUserId)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(1)

                        // Execute both queries with listeners
                        var listener1: ListenerRegistration? = null
                        var listener2: ListenerRegistration? = null
                        var latestMessage = ""

                        listener1 = query1.addSnapshotListener { snapshot1, e1 ->
                            if (e1 != null) {
                                Log.e(TAG, "Error fetching query1 for userId $userId: ${e1.message}", e1)
                                return@addSnapshotListener
                            }

                            listener2 = query2.addSnapshotListener { snapshot2, e2 ->
                                if (e2 != null) {
                                    Log.e(TAG, "Error fetching query2 for userId $userId: ${e2.message}", e2)
                                    return@addSnapshotListener
                                }

                                latestMessage = when {
                                    snapshot1 == null || snapshot1.isEmpty && (snapshot2 == null || snapshot2.isEmpty) -> ""
                                    snapshot1 == null || snapshot1.isEmpty -> snapshot2?.documents?.get(0)?.getString("text") ?: ""
                                    snapshot2 == null || snapshot2.isEmpty -> snapshot1.documents[0].getString("text") ?: ""
                                    else -> {
                                        val time1 = snapshot1.documents[0].getLong("timestamp") ?: 0
                                        val time2 = snapshot2.documents[0].getLong("timestamp") ?: 0
                                        if (time1 >= time2) snapshot1.documents[0].getString("text") ?: ""
                                        else snapshot2.documents[0].getString("text") ?: ""
                                    }
                                }
                                Log.d(TAG, "userId: $userId, lastMessage: $latestMessage")

                                // Update or add ChatItem
                                val existingChat = chats.find { it.userId == userId }
                                if (existingChat != null) {
                                    chats[chats.indexOf(existingChat)] = ChatItem(userId, username, latestMessage)
                                } else {
                                    chats.add(ChatItem(userId, username, latestMessage))
                                }
                                chats.sortBy { it.username }
                                chatAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching user $userId: ${e.message}", e)
                    // Skip failed user
                }
        }
    }
}