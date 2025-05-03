package com.example.tradesphere

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NewPostActivity : AppCompatActivity() {

    private lateinit var etPostContent: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnAddImage: Button
    private lateinit var btnPost: Button

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_newpost)  // Or activity_new_post.xml if named differently

        etPostContent = findViewById(R.id.etPostContent)
        imgPreview = findViewById(R.id.imgPreview)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnPost = findViewById(R.id.btnPost)

        btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnPost.setOnClickListener {
            val postText = etPostContent.text.toString().trim()
            if (postText.isEmpty()) {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
            } else {
                // Here you would save to Firebase or local database
                Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity and go back
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
}
