package com.example.tradesphere

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

class AddProductFragment : Fragment() {

    private lateinit var btnAddImages: Button
    private lateinit var etProductTitle: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var btnSubmitProduct: Button
    private var selectedImageUris: List<Uri> = emptyList()

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImageUris = uris
        Toast.makeText(requireContext(), "${uris.size} image(s) selected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addproduct, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        btnAddImages = view.findViewById(R.id.btnAddImages)
        etProductTitle = view.findViewById(R.id.etProductTitle)
        etProductPrice = view.findViewById(R.id.etProductPrice)
        etProductDescription = view.findViewById(R.id.etProductDescription)
        btnSubmitProduct = view.findViewById(R.id.btnSubmitProduct)

        // Launch image picker on button click
        btnAddImages.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Handle product submission
        btnSubmitProduct.setOnClickListener {
            val title = etProductTitle.text.toString().trim()
            val price = etProductPrice.text.toString().trim()
            val description = etProductDescription.text.toString().trim()

            if (title.isEmpty() || price.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUris.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulate product upload
            Toast.makeText(requireContext(), "Product '$title' added successfully!", Toast.LENGTH_LONG).show()

            // Optional: clear fields after submission
            etProductTitle.text.clear()
            etProductPrice.text.clear()
            etProductDescription.text.clear()
            selectedImageUris = emptyList()
        }
    }
}
