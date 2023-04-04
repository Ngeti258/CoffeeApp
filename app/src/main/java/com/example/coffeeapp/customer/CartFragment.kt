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
    private lateinit var confirmPurchase : Button

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        confirmPurchase.setOnClickListener {
            val ordersRef = database.reference.child("orders")
            for (product in productList) {
                val order = Order(
                    userId = userId,
                    productId = product.productId,
                    productName = product.coffeeType,
                    productPrice = product.price,
                    productQuantity = product.quantity,
                    farmerId = product.userId,
                    orderDate = System.currentTimeMillis().toString()
                )
                product.userId?.let { it1 ->
                    ordersRef.child(it1).push().setValue(order).addOnSuccessListener {
                        Toast.makeText(
                            activity?.applicationContext,
                            "Orders placed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                databaseRef.removeValue()            }
        }
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
    data class Order(
        val userId: String?,
        val productId: String?,
        val productName: String?,
        val productPrice: Double?,
        val farmerId : String?,
        val productQuantity: Double?,
        var orderDate:String?
        )

}
