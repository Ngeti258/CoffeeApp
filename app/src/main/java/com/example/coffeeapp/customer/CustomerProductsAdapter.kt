package com.example.coffeeapp.customer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeapp.R
import com.example.coffeeapp.Signup
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat
import java.util.*

class CustomerProductsAdapter(
    private val context: Context,
    private var productList: List<Product>,
    private var userList: List<Signup.User>,

    ) : RecyclerView.Adapter<CustomerProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.customer_products_layout, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
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
            val cartItem = product.productId?.let { it1 ->
                CartItem(
                    product.coffeeType,
                    product.coffeeGrade,
                    product.price,
                    product.quantity,
                    product.imageUrl,
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    holder.farmName.text.toString(),
                    it1,
                    product.userId,
                )
            }

            // add the cart item to the database
            val cartRef = FirebaseDatabase.getInstance().getReference("carts")
            val cartProductId = cartRef.push().key
            if (cartItem != null) {
                cartItem.cartProductId = cartProductId
            }
            FirebaseAuth.getInstance().currentUser?.uid?.let { it3 ->
                product.productId?.let { it1 ->
                    if (cartProductId != null) {
                        cartRef.child(it3).child(cartProductId).setValue(cartItem)
                            .addOnSuccessListener {
                                // Add the item to the local productList and notify the adapter
                                productList = productList.toMutableList().apply { add(product) }
                                notifyItemInserted(productList.lastIndex)
                                Toast.makeText(
                                    context,
                                    "Added to cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
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
        product.userId?.let {
            FirebaseDatabase.getInstance().reference.child("user").child(it).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java)
                        holder.farmName.text = name
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }


    }


    override fun getItemCount() = productList.size


        fun setFilteredList(productList: List<Product>){
            this.productList = productList
            notifyDataSetChanged()

        }



    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coffeeTypeTV: TextView = itemView.findViewById(R.id.coffeeTypeTV)
        val coffeeGradeTV: TextView = itemView.findViewById(R.id.coffeeGradeTV)
        val priceTV: TextView = itemView.findViewById(R.id.coffeePriceTV)
        val productIV: ImageView = itemView.findViewById(R.id.imageView)
        val addButton: Button = itemView.findViewById(R.id.add_to_cart)
        var farmName : TextView = itemView.findViewById(R.id.farmerNameTV)
    }
}

class CartItem(
    coffeeType: String?,
    coffeeGrade: String?,
    price: Double?,
    quantity: Double?,
    imageUrl: String?,
    s: String,
    name: String,
    productId: String?,
    farmerId: String?
) {
    val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    val farmerId: String? = farmerId
    val coffeeGrade: String? = coffeeGrade
    val price : Double? =price
    val name : String? = name
    val imageUrl : String? = imageUrl
    val coffeeType: String? = coffeeType
    val productId : String? = productId
    var cartProductId : String? = null // add cartProductId as a property
}