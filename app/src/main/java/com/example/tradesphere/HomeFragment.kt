package com.example.tradesphere.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.R
import com.example.tradesphere.adapters.PostAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerPosts: RecyclerView
    private lateinit var btnNewPost: ImageButton
    private lateinit var btnAll: Button
    private lateinit var btnElectronics: Button
    private lateinit var btnClothing: Button
    private lateinit var btnBooks: Button
    private lateinit var tvHomeTitle: TextView

    private var selectedCategory: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recyclerPosts = rootView.findViewById(R.id.recyclerPosts)
        btnNewPost = rootView.findViewById(R.id.btnNewPost)
        btnAll = rootView.findViewById(R.id.btnAll)
        btnElectronics = rootView.findViewById(R.id.btnElectronics)
        btnClothing = rootView.findViewById(R.id.btnClothing)
        btnBooks = rootView.findViewById(R.id.btnBooks)
        tvHomeTitle = rootView.findViewById(R.id.tvHomeTitle)

        // Set up RecyclerView
        recyclerPosts.layoutManager = LinearLayoutManager(context)
        recyclerPosts.adapter = PostAdapter(emptyList())  // Placeholder for posts

        // Set category filter listeners
        btnAll.setOnClickListener {
            selectedCategory = "All"
            updateCategoryFilter()
            performCategorySearch()
        }
        btnElectronics.setOnClickListener {
            selectedCategory = "Electronics"
            updateCategoryFilter()
            performCategorySearch()
        }
        btnClothing.setOnClickListener {
            selectedCategory = "Clothing"
            updateCategoryFilter()
            performCategorySearch()
        }
        btnBooks.setOnClickListener {
            selectedCategory = "Books"
            updateCategoryFilter()
            performCategorySearch()
        }

        // Handle new post button
        btnNewPost.setOnClickListener {
            // Navigate to the new post screen (handle navigation or show a dialog)
        }

        return rootView
    }

    private fun updateCategoryFilter() {
        // Highlight the selected category
        btnAll.setBackgroundColor(if (selectedCategory == "All") resources.getColor(R.color.purple_500) else resources.getColor(R.color.white))
        btnElectronics.setBackgroundColor(if (selectedCategory == "Electronics") resources.getColor(R.color.purple_500) else resources.getColor(R.color.white))
        btnClothing.setBackgroundColor(if (selectedCategory == "Clothing") resources.getColor(R.color.purple_500) else resources.getColor(R.color.white))
        btnBooks.setBackgroundColor(if (selectedCategory == "Books") resources.getColor(R.color.purple_500) else resources.getColor(R.color.white))
    }

    private fun performCategorySearch() {
        // Replace with real search logic to fetch posts based on the selected category
        val posts = getPostsByCategory(selectedCategory)
        recyclerPosts.adapter = PostAdapter(posts)
    }

    private fun getPostsByCategory(category: String): List<String> {
        // Placeholder method to return mock data based on category
        return when (category) {
            "Electronics" -> listOf("Electronics Post 1", "Electronics Post 2")
            "Clothing" -> listOf("Clothing Post 1", "Clothing Post 2")
            "Books" -> listOf("Book Post 1", "Book Post 2")
            else -> listOf("Post 1", "Post 2", "Post 3")
        }
    }
}
