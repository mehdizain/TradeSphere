class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerSearchResults: RecyclerView
    private lateinit var btnAccounts: Button
    private lateinit var btnPosts: Button

    private var searchResults: MutableList<String> = mutableListOf() // Store search results
    private lateinit var searchAdapter: SearchResultsAdapter
    private var selectedCategory: String = "Accounts"  // Default category is "Accounts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)  // Set the layout file

        // Initialize views
        etSearch = findViewById(R.id.etSearch)
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults)
        btnAccounts = findViewById(R.id.btnAccounts)
        btnPosts = findViewById(R.id.btnPosts)

        // Set up RecyclerView with adapter
        searchAdapter = SearchResultsAdapter(searchResults)
        recyclerSearchResults.layoutManager = LinearLayoutManager(this)
        recyclerSearchResults.adapter = searchAdapter

        // Set listeners for category filter buttons
        btnAccounts.setOnClickListener {
            selectedCategory = "Accounts"
            filterResults()  // Update the results based on the selected category
        }

        btnPosts.setOnClickListener {
            selectedCategory = "Posts"
            filterResults()  // Update the results based on the selected category
        }

        // Set listener for search query input
        etSearch.addTextChangedListener { text ->
            filterResults()  // Filter results whenever the user types something
        }
    }

    private fun filterResults() {
        // Sample data - Replace with actual data filtering logic
        val allResults = when (selectedCategory) {
            "Accounts" -> listOf("Account 1", "Account 2", "Account 3")
            "Posts" -> listOf("Post 1", "Post 2", "Post 3")
            else -> emptyList()
        }

        // Filter results based on the query entered in the search bar
        val query = etSearch.text.toString().lowercase()
        searchResults.clear()

        allResults.filter { it.lowercase().contains(query) }.let {
            searchResults.addAll(it)
        }

        // Notify the adapter that the data has changed
        searchAdapter.notifyDataSetChanged()
    }
}
