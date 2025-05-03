package com.example.tradesphere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this) // Ensures Firebase is initialized
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()



        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)
        val btnSignup: Button = findViewById(R.id.btnSignup)
        val tvLoginRedirect: TextView = findViewById(R.id.tvLoginRedirect)

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase signup
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Set the displayName for the user
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // After updating displayName, store the additional data in Firestore
                                    val userId = user.uid
                                    val userData = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "description" to "",
                                        "followers" to 0,
                                        "following" to 0
                                    )

                                    db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, LoginActivity::class.java))  // Navigate to login
                                            finish()  // Close signup activity
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Failed to set display name", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val error = task.exception?.message ?: "Signup failed"
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        tvLoginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))  // Redirect to login if needed
        }
    }
}
