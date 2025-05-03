import android.os.Bundle
import android.view.View
import com.example.tradesphere.R
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var imgLogo: ImageView
    private lateinit var tvAppTitle: TextView
    private lateinit var ivProductImage: ImageView
    private lateinit var tvProductTitle: TextView
    private lateinit var tvProductDescription: TextView
    private lateinit var tvProductPrice: TextView
    private lateinit var etComment: EditText
    private lateinit var btnBuy: Button
    private lateinit var btnStartChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_productdetails)

        // Initialize the views
        imgLogo = findViewById(R.id.imgLogo)
        tvAppTitle = findViewById(R.id.tvAppTitle)
        ivProductImage = findViewById(R.id.ivProductImage)
        tvProductTitle = findViewById(R.id.tvProductTitle)
        tvProductDescription = findViewById(R.id.tvProductDescription)
        tvProductPrice = findViewById(R.id.tvProductPrice)
        etComment = findViewById(R.id.etComment)
        btnBuy = findViewById(R.id.btnBuy)
        btnStartChat = findViewById(R.id.btnStartChat)

        // Set product data (this can be passed via Intent or API call)
        val productTitle = "Brown Leather Shoes"
        val productDescription = "An imported top branded shoe having fine leather quality along with wear-back warranty."
        val productPrice = "Rs. 5000"

        // Set values dynamically (you can replace these with data from your API or database)
        tvProductTitle.text = productTitle
        tvProductDescription.text = productDescription
        tvProductPrice.text = productPrice

        // Set button click listeners
        btnBuy.setOnClickListener {
            // Handle buy action
            // For example, you can navigate to a payment screen
        }

        btnStartChat.setOnClickListener {
            // Handle start chat action
            // You can start a chat activity here (if applicable)
        }
    }
}
