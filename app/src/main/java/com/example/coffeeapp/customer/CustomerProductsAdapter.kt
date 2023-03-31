package com.example.coffeeapp.customer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeapp.R
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class CustomerProductsAdapter(
    private val context: Context,
    private var productList: List<Product>
) : RecyclerView.Adapter<CustomerProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.customer_products_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = productList[position]

        holder.coffeeTypeTV.text = product.coffeeType
        holder.coffeeGradeTV.text = " ${product.coffeeGrade}"

        val priceFormat = DecimalFormat("Ksh#,###.## per kilogram")
        val formattedPrice = priceFormat.format(product.price)
        holder.priceTV.text = formattedPrice

        // Load product image using Glide library
        if (product.imageUrl != null && product.imageUrl!!.isNotBlank()) {
            Glide.with(context).load(product.imageUrl).into(holder.productIV)
        } else {
            holder.productIV.setImageResource(R.drawable.baseline_add_photo_alternate_24)
        }

        holder.addButton.setOnClickListener {
            val cartItem = CartItem(
                product.coffeeType,
                product.coffeeGrade,
                product.price,
                product.quantity,
                product.imageUrl,
                FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )

            // add the cart item to the database
            val cartRef = FirebaseDatabase.getInstance().getReference("carts")
            cartItem.userId.let { it1 ->
                cartItem.coffeeType.let { it2 ->

                        if (it1 != null) {
                            if (it2 != null) {
                                cartRef.push().setValue(cartItem)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Added to cart successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val cartFragment = CartFragment.newInstance()
                                        cartFragment.show(
                                            (context as FragmentActivity).supportFragmentManager,
                                            "CartFragment"
                                        )
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to add to cart: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }

            }
        }


    }

    override fun getItemCount() = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coffeeTypeTV: TextView = itemView.findViewById(R.id.coffeeTypeTV)
        val coffeeGradeTV: TextView = itemView.findViewById(R.id.coffeeGradeTV)
        val priceTV: TextView = itemView.findViewById(R.id.coffeePriceTV)
        val productIV: ImageView = itemView.findViewById(R.id.imageView)
        val addButton: Button = itemView.findViewById(R.id.add_to_cart)
    }
}

class CartItem(coffeeType: String?, coffeeGrade: String?, price: Double?, quantity: Double?, imageUrl: String?, s: String) {


    val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    val coffeeGrade: String? = coffeeGrade
    val price : Double? = price
    val imageUrl : String? = imageUrl
    val coffeeType: String? = coffeeType

}
