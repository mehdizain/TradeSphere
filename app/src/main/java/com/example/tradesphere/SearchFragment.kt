package com.example.tradesphere.fragments

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
import com.example.tradesphere.R
import com.example.tradesphere.adapters.SearchResultAdapter
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
    private var usersList = mutableListOf<String>()
    private var postsList = mutableListOf<String>()

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
                    val name = document.getString("name") ?: "Unknown"
                    usersList.add(name)
                }
                // Perform search again after loading data
                performSearch()
            }

        // Fetch posts from Firestore
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                postsList.clear()
                for (document in documents) {
                    val title = document.getString("title") ?: "Untitled"
                    postsList.add(title)
                }
                // Perform search again after loading data
                performSearch()
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
        val results = if (selectedCategory == "Accounts") {
            usersList.filter { it.contains(searchQuery, ignoreCase = true) }
        } else {
            postsList.filter { it.contains(searchQuery, ignoreCase = true) }
        }

        recyclerSearchResults.adapter = SearchResultAdapter(results)
    }
}
