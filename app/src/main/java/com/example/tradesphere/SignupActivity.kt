package com.yourpackage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tradesphere.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)
        val btnSignup: Button = findViewById(R.id.btnSignup)
        val tvLoginRedirect: TextView = findViewById(R.id.tvLoginRedirect)

        // Set up Signup button click listener
        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Check if all fields are filled
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                // Here you can handle actual signup logic (e.g., saving to a database, Firebase authentication, etc.)
            }
        }

        // Set up "Already have an account? Login" click listener
        tvLoginRedirect.setOnClickListener {
            // Handle redirection to Login Activity
            // For example:
            // startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Redirect to Login", Toast.LENGTH_SHORT).show()
        }
    }
}
