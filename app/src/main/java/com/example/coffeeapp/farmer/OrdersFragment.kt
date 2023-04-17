package com.example.coffeeapp.farmer

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class OrdersFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var OrdersList: MutableList<Orders>
    private lateinit var coffeeTypeDropdown: AutoCompleteTextView
    private lateinit var coffeeGradeEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var auth: FirebaseAuth

    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    private lateinit var adapter: OrdersAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_orders, container, false)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("orders")

        OrdersList = mutableListOf()
        adapter = OrdersAdapter(requireContext(), OrdersList)
        productsRecyclerView = rootView.findViewById(R.id.orders_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter




        // Attach listener to databaseRef
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                OrdersList.clear()
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(OrdersFragment.Orders::class.java)
                    if (order != null && order.farmerId == currentUser) {
                        OrdersList.add(order)
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
    data class Orders(
        val coffeeType: String? = null,
        val coffeeGrade: String? = null,
        val quantity: Double? = 0.0,
        val price: Double? = 0.0,
        val userId: String? = null,
        var imageUrl: String? = null,
        var productId: String? = null,
        var farmerId: String? = null,
        var orderId: String? = null,

    )

}

