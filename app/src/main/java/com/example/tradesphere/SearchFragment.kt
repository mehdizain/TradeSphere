package com.example.tradesphere.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.adapters.SearchResultAdapter

class SearchFragment : Fragment() {

    // Declare views
    private lateinit var etSearch: EditText
    private lateinit var recyclerSearchResults: RecyclerView
    private lateinit var btnAccounts: Button
    private lateinit var btnPosts: Button

    // Variables to hold the selected filter and search query
    private var selectedCategory: String = "Accounts" // Default category
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize views
        etSearch = rootView.findViewById(R.id.etSearch)
        recyclerSearchResults = rootView.findViewById(R.id.recyclerSearchResults)
        btnAccounts = rootView.findViewById(R.id.btnAccounts)
        btnPosts = rootView.findViewById(R.id.btnPosts)

        // Set up RecyclerView
        recyclerSearchResults.layoutManager = LinearLayoutManager(context)
        recyclerSearchResults.adapter = SearchResultAdapter(emptyList()) // Placeholder adapter

        // Handle category selection (Accounts, Posts)
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

        // Handle search input
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                performSearch()
            }
        })

        return rootView
    }

    private fun updateCategoryFilter() {
        // Highlight the selected category button
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
        // Perform the search based on the selected category and query
        val results = getSearchResults(selectedCategory, searchQuery)
        recyclerSearchResults.adapter = SearchResultAdapter(results)
    }

    private fun getSearchResults(category: String, query: String): List<String> {
        // This is just a placeholder method.
        // You should replace it with actual logic to fetch search results (e.g., from a database or API).
        val mockResults = listOf(
            "$category Result 1 for '$query'",
            "$category Result 2 for '$query'",
            "$category Result 3 for '$query'"
        )
        return mockResults
    }
}
