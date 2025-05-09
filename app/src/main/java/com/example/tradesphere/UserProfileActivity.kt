package com.example.tradesphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var tvUserUsername: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserDescription: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPhone: TextView
    private lateinit var tvUserFollowers: TextView
    private lateinit var tvUserFollowing: TextView
    private lateinit var btnFollow: Button
    private lateinit var btnStartChat: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "UserProfileActivity"
    private var followersListener: ListenerRegistration? = null
    private var followStatusListener: ListenerRegistration? = null
    private var targetUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Initialize the views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        tvUserUsername = findViewById(R.id.tvUserUsername)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserDescription = findViewById(R.id.tvUserDescription)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserPhone = findViewById(R.id.tvUserPhone)
        tvUserFollowers = findViewById(R.id.tvUserFollowers)
        tvUserFollowing = findViewById(R.id.tvUserFollowing)
        btnFollow = findViewById(R.id.btnFollow)
        btnStartChat = findViewById(R.id.btnStartChat)

        // Get user data from Intent
        val username = intent.getStringExtra("username") ?: ""

        // Set initial user data
        tvUserUsername.text = username
        tvUserName.text = "Loading..."
        tvUserDescription.text = "Loading..."
        tvUserEmail.text = "Loading..."
        tvUserPhone.text = "Loading..."
        tvUserFollowers.text = "Followers: Loading..."
        tvUserFollowing.text = "Following: Loading..."
        btnFollow.text = "Loading..."

        // Load user data from Firestore
        if (username.isNotEmpty()) {
            loadUserFromFirestore(username)
        } else {
            Log.e(TAG, "No username provided, cannot load user data")
            displayErrorState()
        }

        // Set button click listeners
        btnFollow.setOnClickListener {
            val currentUserId = auth.currentUser?.uid
            if (currentUserId == null) {
                Toast.makeText(this, "Please log in to follow", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (targetUserId == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentUserId == targetUserId) {
                Toast.makeText(this, "Cannot follow yourself", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            toggleFollow(currentUserId, targetUserId!!)
        }

        btnStartChat.setOnClickListener {
            if (username.isNotEmpty()) {
                firestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents[0]
                            val receiverId = document.id
                            val currentUserId = auth.currentUser?.uid
                            if (currentUserId != null && receiverId != currentUserId) {
                                val intent = Intent(this, ChatActivity::class.java).apply {
                                    putExtra("currentUserId", currentUserId)
                                    putExtra("receiverId", receiverId)
                                    putExtra("receiverUsername", username)
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Cannot chat with yourself", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching user ID: ${e.message}", e)
                        Toast.makeText(this, "Failed to start chat", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No user selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove Firestore listeners to prevent memory leaks
        followersListener?.remove()
        followStatusListener?.remove()
    }

    private fun loadUserFromFirestore(username: String) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    targetUserId = document.id
                    val description = document.getString("description") ?: "No description"
                    val email = document.getString("email") ?: "No email"
                    val followers = document.getLong("followers") ?: 0
                    val following = document.getLong("following") ?: 0
                    val name = document.getString("name") ?: "Unknown"
                    val phone = document.getString("phone") ?: "No phone"

                    // Update UI
                    tvUserUsername.text = username
                    tvUserName.text = name
                    tvUserDescription.text = description
                    tvUserEmail.text = email
                    tvUserPhone.text = phone
                    tvUserFollowers.text = "Followers: $followers"
                    tvUserFollowing.text = "Following: $following"

                    // Set up real-time listener for followers count
                    followersListener = firestore.collection("users")
                        .document(targetUserId!!)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                Log.e(TAG, "Error fetching followers: ${e.message}", e)
                                tvUserFollowers.text = "Followers: Error"
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                val updatedFollowers = snapshot.getLong("followers") ?: 0
                                tvUserFollowers.text = "Followers: $updatedFollowers"
                            } else {
                                tvUserFollowers.text = "Followers: 0"
                            }
                        }

                    // Check follow status
                    val currentUserId = auth.currentUser?.uid
                    if (currentUserId != null && currentUserId != targetUserId) {
                        followStatusListener = firestore.collection("users")
                            .document(targetUserId!!)
                            .collection("followers")
                            .document(currentUserId)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.e(TAG, "Error checking follow status: ${e.message}", e)
                                    btnFollow.text = "Follow"
                                    return@addSnapshotListener
                                }
                                btnFollow.text = if (snapshot != null && snapshot.exists()) "Unfollow" else "Follow"
                            }
                    } else {
                        btnFollow.text = "Follow"
                        btnFollow.isEnabled = false // Disable for self or unauthenticated
                    }
                } else {
                    Log.d(TAG, "User document doesn't exist")
                    displayErrorState()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting user document: ${e.message}", e)
                displayErrorState()
                Toast.makeText(this, "Failed to load user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFollow(currentUserId: String, targetUserId: String) {
        Log.d(TAG, "Toggling follow: currentUserId=$currentUserId, targetUserId=$targetUserId")
        // Check current follow status
        val followRef = firestore.collection("users")
            .document(targetUserId)
            .collection("followers")
            .document(currentUserId)

        followRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Unfollow
                    firestore.runTransaction { transaction ->
                        // Remove from followers subcollection
                        transaction.delete(followRef)
                        // Decrement target user's followers
                        transaction.update(
                            firestore.collection("users").document(targetUserId),
                            "followers",
                            FieldValue.increment(-1)
                        )
                        // Decrement current user's following
                        transaction.update(
                            firestore.collection("users").document(currentUserId),
                            "following",
                            FieldValue.increment(-1)
                        )
                    }.addOnSuccessListener {
                        Log.d(TAG, "Unfollowed successfully")
                        Toast.makeText(this, "Unfollowed!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error unfollowing: ${e.message}", e)
                        Toast.makeText(this, "Failed to unfollow: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Follow
                    firestore.runTransaction { transaction ->
                        // Add to followers subcollection
                        transaction.set(followRef, mapOf("followerId" to currentUserId))
                        // Increment target user's followers
                        transaction.update(
                            firestore.collection("users").document(targetUserId),
                            "followers",
                            FieldValue.increment(1)
                        )
                        // Increment current user's following
                        transaction.update(
                            firestore.collection("users").document(currentUserId),
                            "following",
                            FieldValue.increment(1)
                        )
                    }.addOnSuccessListener {
                        Log.d(TAG, "Followed successfully")
                        Toast.makeText(this, "Followed!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error following: ${e.message}", e)
                        Toast.makeText(this, "Failed to follow: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking follow status: ${e.message}", e)
                Toast.makeText(this, "Failed to process follow: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayErrorState() {
        tvUserName.text = "Error"
        tvUserDescription.text = "Unable to load user details"
        tvUserEmail.text = "N/A"
        tvUserPhone.text = "N/A"
        tvUserFollowers.text = "Followers: N/A"
        tvUserFollowing.text = "Following: N/A"
        btnFollow.text = "Follow"
        btnFollow.isEnabled = false
    }
}