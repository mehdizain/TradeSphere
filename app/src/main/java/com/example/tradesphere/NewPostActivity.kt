package com.example.tradesphere

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class NewPostActivity : AppCompatActivity() {

    private lateinit var etPostTitle: EditText
    private lateinit var etPostContent: EditText
    private lateinit var etPrice: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnAddImage: Button
    private lateinit var btnPost: Button
    private lateinit var spinnerCategory: Spinner

    private var selectedImageUri: Uri? = null

    private val storageRef = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_newpost)

        etPostTitle = findViewById(R.id.etPostTitle)
        etPostContent = findViewById(R.id.etPostContent)
        etPrice = findViewById(R.id.etPrice)
        imgPreview = findViewById(R.id.imgPreview)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnPost = findViewById(R.id.btnPost)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Set up category spinner
        val categories = listOf("Electronics", "Furniture", "Books", "Clothing", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Image picker
        btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Post button logic
        btnPost.setOnClickListener {
            val postTitle = etPostTitle.text.toString().trim()
            val postText = etPostContent.text.toString().trim()
            val priceText = etPrice.text.toString().trim()
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (postTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (postText.isEmpty()) {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (priceText.isEmpty()) {
                Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert price to Double, handle potential formatting issues
            val price = try {
                priceText.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCategory.isEmpty()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                uploadImageAndSavePost(postTitle, postText, price, selectedImageUri!!, selectedCategory)
            } else {
                savePostToFirestore(postTitle, postText, price, null, selectedCategory)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imgPreview.setImageURI(selectedImageUri)
            imgPreview.visibility = ImageView.VISIBLE
        }
    }

    private fun uploadImageAndSavePost(title: String, text: String, price: Double, imageUri: Uri, category: String) {
        val fileName = "post_images/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    savePostToFirestore(title, text, price, uri.toString(), category)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePostToFirestore(title: String, text: String, price: Double, imageUrl: String?, category: String) {
        val userId = auth.currentUser?.uid ?: return

        val postData = hashMapOf(
            "userId" to userId,
            "title" to title,
            "text" to text,
            "price" to price,
            "imageUrl" to imageUrl,
            "category" to category,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("posts")
            .add(postData)
            .addOnSuccessListener {
                Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}