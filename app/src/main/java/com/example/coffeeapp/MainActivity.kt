package com.example.coffeeapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeapp.farmer.Products
import com.example.coffeeapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
class MainActivity : AppCompatActivity() {
    private lateinit var edtGrade : EditText
    private lateinit var edtQuantity : EditText
    private lateinit var edtPrice : EditText
    private lateinit var btnPost : Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var productRecyclerView: RecyclerView
    //private lateinit var productAdapter: com.example.coffeeapp.Famer.ProductAdapter
    private lateinit var productList : ArrayList<Products>
    private lateinit var userID: String
    private lateinit var binding: ActivityMainBinding
    private lateinit var coffeeType: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roles = listOf("Arabica","Robusta","Liberica","Excelsa")
        val adapter = ArrayAdapter(this, R.layout.coffee_list_view, roles)
        binding.dropdownField.setAdapter(adapter)


        productRecyclerView = findViewById(R.id.product_recyclerView)
        coffeeType = findViewById(R.id.dropdown_field)
        edtGrade = findViewById(R.id.edt_grade)
        mAuth = FirebaseAuth.getInstance()
        edtQuantity = findViewById(R.id.edt_quantity)
        edtPrice = findViewById(R.id.edt_price)
        btnPost = findViewById(R.id.btn_post)
        productList = ArrayList()
        //productAdapter = com.example.coffeeapp.Famer.ProductAdapter(this,productList)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        //productRecyclerView.adapter = productAdapter

        mDbRef = Firebase.database.reference
        mDbRef.child("products").child("productID").addValueEventListener(
            object:ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (postSnapshot in snapshot.children) {
                        val product = postSnapshot.getValue(Products::class.java)
                        productList.add(product!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
                )
    }
}