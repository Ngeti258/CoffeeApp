package com.example.coffeeapp.customer

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeapp.R
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat

class CustomerProductsAdapter(private val context: Context, var productList: List<Product>) :
    RecyclerView.Adapter<CustomerProductsAdapter.ProductViewHolder>(){
    private lateinit var database: FirebaseDatabase
    private lateinit var coffeeTypeDropdown: AutoCompleteTextView
    private lateinit var coffeeGradeEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var auth: FirebaseAuth

    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    private lateinit var adapter: CustomerProductsAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference


    private var onItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(product: Product) {

//            val fragment = ProductsFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.framelayout,fragment).commit()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.customer_products_layout, parent, false)
        val addButton = view.findViewById<Button>(R.id.add_to_cart)
        addButton.setOnClickListener(){
            if (imageUri != null) {
                val storageRef = storage.reference
                val imageRef = storageRef.child("product_images")
                val uploadTask = imageRef.putFile(imageUri!!)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        // Add download URL to product object
                        //product.imageUrl = downloadUri.toString()
                        // Add product to Firebase Realtime Database
                        val productsRef = database.getReference("orders")
                        productsRef.push().setValue("orders")
//                            .addOnSuccessListener {
//                                Toast.makeText(
//                                    activity?.applicationContext,
//                                    "added to cart successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//
//                            }

                    }
                }
            }

        }


        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = productList[position]

        holder.coffeeTypeTV.text = product.coffeeType
        holder.coffeeGradeTV.text = " ${product.coffeeGrade}"

//        val quantityFormat = DecimalFormat("#,###.#")
//        val formattedQuantity = quantityFormat.format(product.quantity)
//        holder.quantityTextView.text = "${formattedQuantity}kg"

        val priceFormat = DecimalFormat("Ksh#,###.## per kilogram")
        val formattedPrice = priceFormat.format(product.price)
        holder.priceTV.text = formattedPrice

        holder.productIV.setOnClickListener() {
            onItemClickListener.onItemClick(product)

        }

        // Load product image using Glide library
        if (product.imageUrl != null && product.imageUrl!!.isNotBlank()) {
            Glide.with(context).load(product.imageUrl).into(holder.productIV)
        } else {
            holder.productIV.setImageResource(R.drawable.baseline_add_photo_alternate_24)
        }
    }




    override fun getItemCount() = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val coffeeTypeTV: TextView = itemView.findViewById(R.id.coffeeTypeTV)
        val coffeeGradeTV: TextView = itemView.findViewById(R.id.coffeeGradeTV)
        val priceTV: TextView = itemView.findViewById(R.id.coffeePriceTV)
        val productIV: ImageView = itemView.findViewById(R.id.imageView)
        val addButton : Button = itemView.findViewById(R.id.add_to_cart)
        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }


    }



}
