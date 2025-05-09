package com.example.tradesphere

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var ivProductImage: ImageView
    private lateinit var tvProductTitle: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var etComment: EditText
    private lateinit var btnBuy: Button
    private lateinit var btnStartChat: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ProductDetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_productdetails)

        // Initialize the views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        ivProductImage = findViewById(R.id.ivProductImage)
        tvProductTitle = findViewById(R.id.tvProductTitle)
        tvProductDescription = findViewById(R.id.tvProductDescription)
        tvProductPrice = findViewById(R.id.tvProductPrice)
        btnBuy = findViewById(R.id.btnBuy)
        btnStartChat = findViewById(R.id.btnStartChat)

        // Get post data from Intent
        val postId = intent.getStringExtra("postId") ?: ""
        val title = intent.getStringExtra("title") ?: "Untitled"
        val description = intent.getStringExtra("text") ?: "No description"
        val imageUrl = intent.getStringExtra("imageUrl")
        val category = intent.getStringExtra("category") ?: "Unknown"

        // Set initial product data
        tvProductTitle.text = title
        tvProductDescription.text = description
        tvProductPrice.text = "Loading price..." // Will be updated from Firestore

        // Load product data from Firestore including price
        if (postId.isNotEmpty()) {
            loadProductFromFirestore(postId)
        } else {
            tvProductPrice.text = "Price not available"
            Log.e(TAG, "No postId provided, cannot load price")
        }

        // Handle image loading
        try {
            if (imageUrl != null && imageUrl.isNotEmpty()) {
                // Use Glide to load remote images
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.sample_product)
                    .error(R.drawable.sample_product)
                    .into(ivProductImage)
                ivProductImage.visibility = View.VISIBLE
            } else {
                ivProductImage.setImageResource(R.drawable.sample_product)
                ivProductImage.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: ${e.message}", e)
            ivProductImage.setImageResource(R.drawable.sample_product)
            ivProductImage.visibility = View.VISIBLE
        }

        // Set button click listeners
        btnBuy.setOnClickListener {
            // Handle buy action (e.g., navigate to payment screen)
            Toast.makeText(this, "Buy feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnStartChat.setOnClickListener {
            // Handle start chat action (e.g., start chat activity)
            Toast.makeText(this, "Chat feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProductFromFirestore(postId: String) {
        firestore.collection("posts")
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Get price from document
                    val price = document.getDouble("price")

                    if (price != null) {
                        // Format price as currency
                        val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(price)
                        tvProductPrice.text = formattedPrice
                    } else {
                        tvProductPrice.text = "Price not available"
                        Log.d(TAG, "No price field found in document")
                    }

                    // Update other fields if needed
                    val title = document.getString("title")
                    val text = document.getString("text")
                    val imageUrl = document.getString("imageUrl")

                    if (title != null) tvProductTitle.text = title
                    if (text != null) tvProductDescription.text = text

                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.sample_product)
                            .error(R.drawable.sample_product)
                            .into(ivProductImage)
                    }
                } else {
                    Log.d(TAG, "Document doesn't exist or is null")
                    tvProductPrice.text = "Price not available"
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting document: ${e.message}", e)
                tvProductPrice.text = "Price not available"
                Toast.makeText(this, "Failed to load product details", Toast.LENGTH_SHORT).show()
            }
    }
}