package com.yourpackage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tradesphere.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                // Perform actual login here (authentication logic)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // For example, navigate to another activity after login
                // startActivity(Intent(this, HomeActivity::class.java))
            }
        }

        // Set up Forgot Password text click listener
        tvForgotPassword.setOnClickListener {
            // Handle forgot password action here
            Toast.makeText(this, "Redirect to Forgot Password", Toast.LENGTH_SHORT).show()
            // For example, navigate to a new screen for resetting the password
            // startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Set up Sign Up redirect text click listener
        tvSignupRedirect.setOnClickListener {
            // Redirect to Signup Activity
            // For example:
            // startActivity(Intent(this, SignupActivity::class.java))
            Toast.makeText(this, "Redirect to Signup", Toast.LENGTH_SHORT).show()
        }
    }
}
