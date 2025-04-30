class SearchResultsAdapter(private val searchResults: List<String>) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    // Create a ViewHolder class to hold the views
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResult: TextView = itemView.findViewById(R.id.tvResult) // Find the TextView for result item
    }

    // Called when a new ViewHolder is created (inflate item layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvResult.text = searchResults[position]
    }

    // Return the total number of items in the search results
    override fun getItemCount(): Int {
        return searchResults.size
    }
}
