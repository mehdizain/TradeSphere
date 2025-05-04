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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AllChatsFragment : Fragment() {

    // Declare views
    private lateinit var tvAppTitle: TextView
    private lateinit var tvChatsHeader: TextView
    private lateinit var recyclerChats: RecyclerView
    private lateinit var imgLogo: ImageView

    // Firebase instance
    private lateinit var database: FirebaseDatabase
    private lateinit var chatsRef: DatabaseReference

    // Data holder for chats
    private val chatList: MutableList<String> = mutableListOf()

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

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        chatsRef = database.reference.child("chats")

        // Fetch chat data from Firebase
        fetchChats()

        return rootView
    }

    private fun fetchChats() {
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear previous chat list and fetch new data
                chatList.clear()

                // Iterate over the snapshot and extract chat names
                for (chatSnapshot in dataSnapshot.children) {
                    val chatName = chatSnapshot.child("name").getValue(String::class.java)
                    chatName?.let {
                        chatList.add(it)
                    }
                }

                // Notify adapter with the updated list
                recyclerChats.adapter = ChatAdapter(chatList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here (e.g., no connection, etc.)
            }
        })
    }
}
