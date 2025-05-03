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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this) // Ensures Firebase is initialized
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Initialize views
        val etLoginEmail: EditText = findViewById(R.id.etLoginEmail)
        val etLoginPassword: EditText = findViewById(R.id.etLoginPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val tvForgotPassword: TextView = findViewById(R.id.tvForgotPassword)
        val tvSignupRedirect: TextView = findViewById(R.id.tvSignupRedirect)

        // Set up Login button click listener
        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            // Check if both fields are filled
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Firebase authentication: sign in with email and password
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login success
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java)) // Go to the main activity
                            finish()  // Close the login activity
                        } else {
                            // Login failure
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Set up Forgot Password text click listener
        tvForgotPassword.setOnClickListener {
            // Handle forgot password action (you could redirect to a separate activity or show a dialog)
            Toast.makeText(this, "Redirect to Forgot Password", Toast.LENGTH_SHORT).show()
        }

        // Set up Sign Up redirect text click listener
        tvSignupRedirect.setOnClickListener {
            // Redirect to Signup Activity
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
