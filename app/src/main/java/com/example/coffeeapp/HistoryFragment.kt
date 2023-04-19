package com.example.coffeeapp.farmer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.HistoryAdapter
import com.example.coffeeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var orderHistoryList: MutableList<OrderHistory>
    private lateinit var adapter: HistoryAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var deleteHistory :Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)

        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("history")

        orderHistoryList = mutableListOf()
        adapter = HistoryAdapter(requireContext(), orderHistoryList)
        productsRecyclerView = rootView.findViewById(R.id.products_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val deleteHistory = rootView.findViewById<Button>(R.id.clear_history)

        deleteHistory.setOnClickListener {
            val historyRef = FirebaseDatabase.getInstance().getReference("history")

            historyRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Orders cleared", Toast.LENGTH_SHORT).show()
                    requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to clear orders: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                orderHistoryList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(OrderHistory::class.java)
                            product?.let { orderHistoryList.add(it) }
                adapter.notifyDataSetChanged()
            }
        }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return rootView
    }
    data class OrderHistory(
        val coffeeType: String? = null,
        val coffeeGrade: String? = null,
        val quantity: Double? = 0.0,
        val price: Double? = 0.0,
        val userId: String? = null,
        var imageUrl: String? = null,
        var productId: String? = null,
        var farmerId: String? = null,
        var orderId: String? = null
    )
}

