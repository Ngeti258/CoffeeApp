package com.example.coffeeapp.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
                    product.userId // This should be the farmer's id, not the user's id
                )
                product.farmerId?.let { it1 ->
                    product.productId?.let { it2 ->
                        ordersRef.child(it1).child(it2).push().setValue(order)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    activity?.applicationContext,
                                    "Orders placed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                databaseRef.removeValue()
            }
        }

        // Assuming you have a cartList that contains the items in the cart

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
        farmerId: String?
    ) {
        val userId: String? = userId
        val farmerId: String? = farmerId
        val coffeeGrade: String? = coffeeGrade
        val price: Double? = price
        val imageUrl: String? = imageUrl
        val coffeeType: String? = coffeeType
        val productId: String? = productId
    }

}