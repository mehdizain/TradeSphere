package com.example.tradesphere.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.tradesphere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseUser

class AccountFragment : Fragment() {

    private lateinit var ivProfilePic: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvFollowers: TextView
    private lateinit var tvFollowing: TextView
    private lateinit var tvPosts: TextView
    private lateinit var btnSave: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_accountinfo, container, false)

        ivProfilePic = rootView.findViewById(R.id.ivProfilePic)
        etName = rootView.findViewById(R.id.etName)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        etDescription = rootView.findViewById(R.id.etDescription)
        tvFollowers = rootView.findViewById(R.id.tvFollowers)
        tvFollowing = rootView.findViewById(R.id.tvFollowing)
        tvPosts = rootView.findViewById(R.id.tvPosts)
        btnSave = rootView.findViewById(R.id.btnSave)

        // Fetch current user data from Firebase Authentication
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Populate the fields with the current user's data
            etName.setText(currentUser.displayName)
            etEmail.setText(currentUser.email)

            // Load profile picture (if any) from Firebase Storage


            // Fetch user data from Firestore (description, followers, following)
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val description = document.getString("description") ?: ""
                        val followers = document.getLong("followers") ?: 0
                        val following = document.getLong("following") ?: 0

                        etDescription.setText(description)
                        tvFollowers.text = "Followers: $followers"
                        tvFollowing.text = "Following: $following"
                    }
                }
        }

        // Handle Save button click
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val description = etDescription.text.toString()

            // Update the display name in Firebase Authentication
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully updated profile
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }

                // Update additional user info in Firestore
                val userData = hashMapOf(
                    "description" to description,
                    "followers" to 0,  // Update this later based on actual followers count
                    "following" to 0   // Update this later based on actual following count
                )

                db.collection("users").document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return rootView
    }

    // Optionally, handle profile picture change (if you want to add functionality for updating profile picture)
    private fun updateProfilePicture(uri: String) {
        val user = auth.currentUser
        val profilePicRef: StorageReference = storage.reference.child("profile_pics/${user?.uid}")
        profilePicRef.putFile(Uri.parse(uri))
            .addOnSuccessListener {
                // Profile picture updated successfully
                Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
    }
}
