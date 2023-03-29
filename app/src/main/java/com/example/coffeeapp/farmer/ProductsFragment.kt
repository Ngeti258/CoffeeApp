package com.example.coffeeapp.farmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProductsFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var productList: MutableList<Product>
    private lateinit var adapter: ProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_products, container, false)
//        val newProduct : Button = rootView.findViewById(R.id.new_product)
//        newProduct.setOnClickListener {
//            val fragment = FarmerProductsFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.framelayout,fragment).commit()
//        }
        val myProducts : Button = rootView.findViewById(R.id.my_products)
        myProducts.setOnClickListener {
            val fragment = ProductsFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout,fragment).commit()
        }



        // Initialize database
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("products")

        // Initialize RecyclerView and ProductAdapter
        productList = mutableListOf()
        adapter = ProductAdapter(requireContext(), productList)
        productsRecyclerView = rootView.findViewById(R.id.products_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Attach listener to databaseRef
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        if(userId == product.userId){
                            product?.let { productList.add(it) }
                        }
                    }
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

