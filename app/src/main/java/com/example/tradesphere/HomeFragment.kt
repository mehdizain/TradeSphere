package com.example.tradesphere


import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var recyclerPosts: RecyclerView
    private lateinit var btnNewPost: ImageButton
    private lateinit var btnAll: Button
    private lateinit var btnElectronics: Button
    private lateinit var btnClothing: Button
    private lateinit var btnBooks: Button

    private lateinit var postAdapter: PostAdapter
    private val dummyPosts = listOf(
        Post("Used Laptop", "Electronics", "Rs. 35,000"),
        Post("Vintage Jacket", "Clothing", "Rs. 2,500"),
        Post("Data Structures Book", "Books", "Rs. 800")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Bind views
        recyclerPosts = view.findViewById(R.id.recyclerPosts)
        btnNewPost = view.findViewById(R.id.btnNewPost)
        btnAll = view.findViewById(R.id.btnAll)
        btnElectronics = view.findViewById(R.id.btnElectronics)
        btnClothing = view.findViewById(R.id.btnClothing)
        btnBooks = view.findViewById(R.id.btnBooks)

        // Setup RecyclerView
        recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(dummyPosts)
        recyclerPosts.adapter = postAdapter

        // Filter buttons
        btnAll.setOnClickListener { postAdapter.updateData(dummyPosts) }
        btnElectronics.setOnClickListener { postAdapter.updateData(dummyPosts.filter { it.category == "Electronics" }) }
        btnClothing.setOnClickListener { postAdapter.updateData(dummyPosts.filter { it.category == "Clothing" }) }
        btnBooks.setOnClickListener { postAdapter.updateData(dummyPosts.filter { it.category == "Books" }) }

        // New Post Button
        btnNewPost.setOnClickListener {
            Toast.makeText(requireContext(), "New Post Clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to post creation screen
        }

        return view
    }

    // ----- Data Model -----
    data class Post(val title: String, val category: String, val price: String)

    // ----- Adapter -----
    class PostAdapter(private var posts: List<Post>) :
        RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
            val tvCategory: TextView = itemView.findViewById(R.id.tvPostCategory)
            val tvPrice: TextView = itemView.findViewById(R.id.tvPostPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_addproduct, parent, false)
            return PostViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            val post = posts[position]
            holder.tvTitle.text = post.title
            holder.tvCategory.text = post.category
            holder.tvPrice.text = post.price
        }

        override fun getItemCount(): Int = posts.size

        fun updateData(newPosts: List<Post>) {
            posts = newPosts
            notifyDataSetChanged()
        }
    }
}
