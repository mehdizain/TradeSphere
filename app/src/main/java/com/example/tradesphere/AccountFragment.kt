package com.example.tradesphere.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.tradesphere.R

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_accountinfo, container, false)

        // Initialize views
        ivProfilePic = rootView.findViewById(R.id.ivProfilePic)
        etName = rootView.findViewById(R.id.etName)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        etDescription = rootView.findViewById(R.id.etDescription)
        tvFollowers = rootView.findViewById(R.id.tvFollowers)
        tvFollowing = rootView.findViewById(R.id.tvFollowing)
        tvPosts = rootView.findViewById(R.id.tvPosts)
        btnSave = rootView.findViewById(R.id.btnSave)

        // Handle Save button click
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val description = etDescription.text.toString()

            // TODO: Save data to backend, Firebase, or shared preferences
            Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }
}
