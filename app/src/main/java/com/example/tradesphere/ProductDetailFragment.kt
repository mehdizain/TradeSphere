package com.example.tradesphere

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var ivProductImage: ImageView
    private lateinit var tvProductTitle: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var etComment: EditText
    private lateinit var btnBuy: Button
    private lateinit var btnStartChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_productdetails)

        // Initialize views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        ivProductImage = findViewById(R.id.ivProductImage)
        tvProductTitle = findViewById(R.id.tvProductTitle)
        tvProductDescription = findViewById(R.id.tvProductDescription)
        tvProductPrice = findViewById(R.id.tvProductPrice)
        etComment = findViewById(R.id.etComment)
        btnBuy = findViewById(R.id.btnBuy)
        btnStartChat = findViewById(R.id.btnStartChat)

        // Set sample product data (this can be dynamic based on product details passed to the activity)
        ivProductImage.setImageResource(R.drawable.sample_product)
        tvProductTitle.text = "Brown Leather Shoes"
        tvProductDescription.text = "An imported top branded shoe having fine leather quality along with wear back warranty. The brown color has grace and shine while you wear it."
        tvProductPrice.text = "Rs. 5000"

        // Handle Buy button click
        btnBuy.setOnClickListener {
            // Handle buying logic, e.g., navigate to the checkout screen or show a message
            Toast.makeText(this, "Proceeding to Buy", Toast.LENGTH_SHORT).show()
        }

        // Handle Start Chat button click
        btnStartChat.setOnClickListener {
            // Handle start chat logic, e.g., open the chat screen
            Toast.makeText(this, "Starting Chat", Toast.LENGTH_SHORT).show()
            // You can use Intent to navigate to the chat activity if needed
            // startActivity(Intent(this, SingleChatActivity::class.java))
        }
    }
}
