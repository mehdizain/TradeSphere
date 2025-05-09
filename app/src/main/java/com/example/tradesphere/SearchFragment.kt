package com.example.tradesphere.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.ProductDetailsActivity
import com.example.tradesphere.R
import com.example.tradesphere.User
import com.example.tradesphere.UserProfileActivity
import com.example.tradesphere.adapters.PostAdapter
import com.example.tradesphere.adapters.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerSearchResults: RecyclerView
    private lateinit var btnAccounts: Button
    private lateinit var btnPosts: Button

    private var selectedCategory: String = "Accounts"
    private var searchQuery: String = ""

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Lists to store fetched data
    private var usersList = mutableListOf<User>()
    private var postsList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        etSearch = rootView.findViewById(R.id.etSearch)
        recyclerSearchResults = rootView.findViewById(R.id.recyclerSearchResults)
        btnAccounts = rootView.findViewById(R.id.btnAccounts)
        btnPosts = rootView.findViewById(R.id.btnPosts)

        recyclerSearchResults.layoutManager = LinearLayoutManager(context)

        // Handle category selection
        btnAccounts.setOnClickListener {
            selectedCategory = "Accounts"
            updateCategoryFilter()
            performSearch()
        }

        btnPosts.setOnClickListener {
            selectedCategory = "Posts"
            updateCategoryFilter()
            performSearch()
        }

        // Search query input
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                performSearch()
            }
        })

        // Fetch initial data from Firestore
        fetchDataFromFirestore()

        return rootView
    }

    private fun fetchDataFromFirestore() {
        // Fetch users from Firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                usersList.clear()
                for (document in documents) {
                    val user = User(
                        description = document.getString("description") ?: "Unknown",
                        email = document.getString("email") ?: "Unknown",
                        followers = document.getLong("followers") ?: 0,
                        following = document.getLong("following") ?: 0,
                        name = document.getString("name") ?: "Unknown",
                        phone = document.getString("phone") ?: "Unknown",
                        username = document.getString("username") ?: "Unknown"
                    )
                    usersList.add(user)
                }
                // Perform search again after loading data
                performSearch()
            }
            .addOnFailureListener { e ->
                // Optional: Handle failure (e.g., show toast)
                performSearch() // Proceed with empty list
            }

        // Fetch posts from Firestore
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                postsList.clear()
                for (document in documents) {
                    val postId = document.id
                    val title = document.getString("title") ?: continue
                    val text = document.getString("text") ?: continue
                    val imageUrl = document.getString("imageUrl")
                    val category = document.getString("category") ?: "Unknown"
                    postsList.add(Post(postId, title, text, category, imageUrl))
                }
                // Perform search again after loading data
                performSearch()
            }
            .addOnFailureListener { e ->
                // Optional: Handle failure (e.g., show toast)
                performSearch() // Proceed with empty list
            }
    }

    private fun updateCategoryFilter() {
        btnAccounts.setBackgroundColor(
            if (selectedCategory == "Accounts") resources.getColor(R.color.purple_500)
            else resources.getColor(R.color.white)
        )
        btnPosts.setBackgroundColor(
            if (selectedCategory == "Posts") resources.getColor(R.color.purple_500)
            else resources.getColor(R.color.white)
        )
    }

    private fun performSearch() {
        if (selectedCategory == "Accounts") {
            val results = usersList.filter {
                it.username.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true)
            }
            recyclerSearchResults.adapter = UserAdapter(results) { user ->
                val intent = Intent(requireContext(), UserProfileActivity::class.java).apply {
                    putExtra("username", user.username)
                }
                startActivity(intent)
            }
        } else {
            val results = postsList.filter { it.title.contains(searchQuery, ignoreCase = true) }
            recyclerSearchResults.adapter = PostAdapter(results) { post ->
                val intent = Intent(requireContext(), ProductDetailsActivity::class.java).apply {
                    putExtra("postId", post.postId)
                    putExtra("title", post.title)
                    putExtra("text", post.text)
                    putExtra("imageUrl", post.imageUrl)
                    putExtra("category", post.category)
                }
                startActivity(intent)
            }
        }
    }
}