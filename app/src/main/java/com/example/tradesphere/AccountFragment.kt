package com.example.tradesphere.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tradesphere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.Task
import android.Manifest

class AccountFragment : Fragment() {

    private lateinit var ivProfilePic: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvFollowers: TextView
    private lateinit var tvFollowing: TextView
    private lateinit var tvPosts: TextView
    private lateinit var btnSave: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var profilePicUri: Uri? = null

    // Activity result launcher for picking profile picture
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            profilePicUri = result.data?.data
            profilePicUri?.let { uri ->
                ivProfilePic.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_accountinfo, container, false)

        // Initialize views
        ivProfilePic = rootView.findViewById(R.id.ivProfilePic)
        etUsername = rootView.findViewById(R.id.etUsername)
        etName = rootView.findViewById(R.id.etName)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPhone = rootView.findViewById(R.id.etPhone)
        etDescription = rootView.findViewById(R.id.etDescription)
        etPassword = rootView.findViewById(R.id.etPassword)
        tvFollowers = rootView.findViewById(R.id.tvFollowers)
        tvFollowing = rootView.findViewById(R.id.tvFollowing)
        tvPosts = rootView.findViewById(R.id.tvPosts)
        btnSave = rootView.findViewById(R.id.btnSave)

        // Fetch and populate user data
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Firebase Auth data
            etName.setText(currentUser.displayName)
            etEmail.setText(currentUser.email)

            // Firestore data
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        etUsername.setText(document.getString("username") ?: "")
                        etPhone.setText(document.getString("phone") ?: "")
                        etDescription.setText(document.getString("description") ?: "")
                        tvFollowers.text = (document.getLong("followers") ?: 0).toString()
                        tvFollowing.text = (document.getLong("following") ?: 0).toString()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Load profile picture
            val profilePicRef = storage.reference.child("profile_pics/${currentUser.uid}")
            profilePicRef.downloadUrl
                .addOnSuccessListener { uri ->
                    ivProfilePic.setImageURI(uri)
                }
                .addOnFailureListener {
                    ivProfilePic.setImageResource(R.drawable.ic_default_profile)
                }

            // Fetch post count
            db.collection("posts")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    tvPosts.text = documents.size().toString()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to fetch posts: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Profile picture click to select image
        ivProfilePic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                pickImageLauncher.launch(intent)
            }
        }

        // Save button click
        btnSave.setOnClickListener {
            saveProfileChanges(currentUser)
        }

        return rootView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        } else {
            Toast.makeText(requireContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileChanges(user: FirebaseUser?) {
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val username = etUsername.text.toString().trim()
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate inputs
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (username.length < 3 || username.length > 20 || !username.matches("[a-zA-Z0-9_]+".toRegex())) {
            Toast.makeText(requireContext(), "Username must be 3-20 characters, letters, numbers, or underscores", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if username is unique (if changed)
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                val currentUsername = document.getString("username") ?: ""
                if (username == currentUsername) {
                    updateUserData(user, username, name, email, phone, description, password)
                } else {
                    db.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                updateUserData(user, username, name, email, phone, description, password)
                            } else {
                                Toast.makeText(requireContext(), "Username already taken", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error checking username: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching current username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserData(
        user: FirebaseUser,
        username: String,
        name: String,
        email: String,
        phone: String,
        description: String,
        password: String
    ) {
        // Update Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { profileTask: Task<Void> ->
                if (!profileTask.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update name: ${profileTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Update email if changed
                    if (email != user.email) {
                        user.updateEmail(email)
                            .addOnCompleteListener { emailTask: Task<Void> ->
                                if (!emailTask.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to update email: ${emailTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                    // Update password if provided
                    if (password.isNotEmpty()) {
                        if (password.length < 6) {
                            Toast.makeText(
                                requireContext(),
                                "Password must be at least 6 characters",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            user.updatePassword(password)
                                .addOnCompleteListener { passwordTask: Task<Void> ->
                                    if (!passwordTask.isSuccessful) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to update password: ${passwordTask.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }

                    // Update Firestore
                    val userData = mapOf<String, Any>(
                        "username" to username,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "description" to description
                        // Do not overwrite followers or following
                    )

                    db.collection("users").document(user.uid)
                        .update(userData)
                        .addOnSuccessListener {
                            // Upload profile picture if changed
                            profilePicUri?.let { uri ->
                                val profilePicRef = storage.reference.child("profile_pics/${user.uid}")
                                profilePicRef.putFile(uri)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to upload profile picture: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } ?: run {
                                Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }
}