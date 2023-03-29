package com.example.coffeeapp.customer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.farmer.Product
import com.example.coffeeapp.farmer.ProductsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CustomerHomeFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var productList: MutableList<Product>
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_customer_home, container, false)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("products")

        productList = mutableListOf()
        adapter = CustomerProductsAdapter(requireContext(), productList)
        productsRecyclerView = rootView.findViewById(R.id.products_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter
        val addToCartButton = productsRecyclerView.findViewById<Button>(R.id.add_to_cart)


        addToCartButton.setOnClickListener(){
            val coffeeType = coffeeTypeDropdown.text.toString()
            val coffeeGrade = coffeeGradeEditText.text.toString()
            val quantity = quantityEditText.text.toString().toDoubleOrNull()
            val price = priceEditText.text.toString().toDoubleOrNull()
            val userId = auth.currentUser?.uid
            val product = Product(coffeeType, coffeeGrade, quantity, price, userId)

            if (imageUri != null) {
                val storageRef = storage.reference
                val imageRef = storageRef.child("product_images/${product.hashCode()}")
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
                        product.imageUrl = downloadUri.toString()
                        // Add product to Firebase Realtime Database
                        val productsRef = database.getReference("orders")
                        productsRef.push().setValue(product)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    activity?.applicationContext,
                                    "added to cart successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val fragment = ProductsFragment()
                                val transaction = parentFragmentManager.beginTransaction()
                                transaction.replace(R.id.frame_layout,fragment).commit()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    activity?.applicationContext,
                                    "Error adding product: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            activity?.applicationContext,
                            "Error uploading image: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }



        // Attach listener to databaseRef
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    productSnapshot.getValue(Product::class.java)?.let { productList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
        return rootView
    }
}

