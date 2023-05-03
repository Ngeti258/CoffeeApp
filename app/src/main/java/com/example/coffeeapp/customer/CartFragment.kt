package com.example.coffeeapp.customer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var productList: MutableList<Product>
    private lateinit var adapter: CartAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var confirmPurchase: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_cart, container, false)

        database = FirebaseDatabase.getInstance()
        databaseRef =
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                database.reference.child("carts").child(
                    it
                )
            }!!

        productList = mutableListOf()
        adapter = CartAdapter(requireContext(), productList)
        productsRecyclerView = rootView.findViewById(R.id.products_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter
        confirmPurchase = rootView.findViewById(R.id.confirm_purchase)


        confirmPurchase = rootView.findViewById(R.id.confirm_purchase)
        val totalPriceTextView = rootView.findViewById<TextView>(R.id.total_price_text_view)
        var totalPrice = 0.0
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val cartRef = database.reference.child("carts").child(currentUser.uid)
            cartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    totalPrice = 0.0
                    for (productSnapshot in dataSnapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product?.price != null) {
                            totalPrice += product.price!!
                        }
                    }
                    totalPriceTextView.text = "Total price: KSH $totalPrice"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        } else {
            // Handle case where user is not logged in
        }





        confirmPurchase.setOnClickListener {
            val ordersRef = database.reference.child("orders")
            for (product in productList) {
                val order = Order(
                    product.coffeeType,
                    product.coffeeGrade,
                    product.price,
                    product.imageUrl,
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    product.productId,
                    product.farmerId,
                    product.orderId
                )
                product.farmerId?.let { it1 ->
                    product.productId?.let { it2 ->
                        val orderId = databaseRef.push().key
                        order.orderId = orderId
                        if (orderId != null) {
                            ordersRef.child(orderId).setValue(order)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        activity?.applicationContext,
                                        "Orders placed successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
                databaseRef.removeValue()
            }
        }


        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
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

    fun show(transaction: FragmentManager, s: String) {

    }

    companion object {
        fun newInstance(): CartFragment {
            return CartFragment()
        }
    }

    class Order(
        coffeeType: String?,
        coffeeGrade: String?,
        price: Double?,
        imageUrl: String?,
        userId: String?,
        productId: String?,
        farmerId: String?,
        orderId:String?
    ) {
        val userId: String? = userId
        val farmerId: String? = farmerId
        val coffeeGrade: String? = coffeeGrade
        val price: Double? = price
        val imageUrl: String? = imageUrl
        val coffeeType: String? = coffeeType
        val productId: String? = productId
        var orderId: String? = orderId
    }
}
