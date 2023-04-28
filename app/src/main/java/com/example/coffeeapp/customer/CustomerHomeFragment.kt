package com.example.coffeeapp.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.R
import com.example.coffeeapp.Signup
import com.example.coffeeapp.farmer.Product
import com.google.firebase.database.*
import java.util.*

class CustomerHomeFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var productList: MutableList<Product>
    private lateinit var userList : MutableList<Signup.User>
    private lateinit var searchView : SearchView

    private lateinit var adapter: CustomerProductsAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userRef: DatabaseReference



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_customer_home, container, false)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("products")
        userRef = database.reference.child("user")


        productList = mutableListOf()
        userList = mutableListOf()
        adapter = CustomerProductsAdapter(requireContext(), productList, userList)
        productsRecyclerView = rootView.findViewById(R.id.products_recyclerview)
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.adapter = adapter
        searchView = rootView.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })



        databaseRef.addValueEventListener(object : ValueEventListener {
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

    private fun filterList(query: String?) {
        if (query != null){
            val filteredList = ArrayList<Product>()
            for (i in productList){
                if (i.coffeeType?.lowercase(Locale.ROOT)?.contains(query) == true) {
                    filteredList.add(i)
                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
            }else{
                adapter.setFilteredList(filteredList)

            }
        }

    }


}



