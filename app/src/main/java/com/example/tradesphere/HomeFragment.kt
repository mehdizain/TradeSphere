package com.example.tradesphere.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradesphere.NewPostActivity
import com.example.tradesphere.R
import com.example.tradesphere.adapters.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tradesphere.ProductDetailsActivity

class HomeFragment : Fragment() {

    private lateinit var recyclerPosts: RecyclerView
    private lateinit var btnNewPost: ImageButton
    private lateinit var btnAll: Button
    private lateinit var btnElectronics: Button
    private lateinit var btnClothing: Button
    private lateinit var btnBooks: Button
    private lateinit var tvHomeTitle: TextView

    private var selectedCategory: String = "All"
    private val firestore = FirebaseFirestore.getInstance()

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
        recyclerPosts.adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(requireContext(), ProductDetailsActivity::class.java).apply {
                putExtra("postId", post.postId)
                putExtra("title", post.title)
                putExtra("text", post.text)
                putExtra("imageUrl", post.imageUrl)
                putExtra("category", post.category)
            }
            startActivity(intent)
        }

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
            val intent = Intent(requireContext(), NewPostActivity::class.java)
            startActivity(intent)
        }

        // Initial load of posts
        performCategorySearch()

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
        // Fetch posts from Firestore based on the selected category
        val postsQuery = when (selectedCategory) {
            "Electronics" -> firestore.collection("posts").whereEqualTo("category", "Electronics")
            "Clothing" -> firestore.collection("posts").whereEqualTo("category", "Clothing")
            "Books" -> firestore.collection("posts").whereEqualTo("category", "Books")
            else -> firestore.collection("posts")
        }

        postsQuery.get()
            .addOnSuccessListener { querySnapshot ->
                // Extract data from Firestore query result
                val posts = querySnapshot.documents.mapNotNull { doc ->
                    val postId = doc.id
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val postText = doc.getString("text") ?: return@mapNotNull null
                    val postImageUrl = doc.getString("imageUrl")
                    val category = doc.getString("category") ?: "Unknown"
                    Post(postId, title, postText, category, postImageUrl)
                }
                recyclerPosts.adapter = PostAdapter(posts) { post ->
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
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}