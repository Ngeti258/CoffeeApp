package com.example.coffeeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.coffeeapp.databinding.ActivityFarmerProductsBinding
import com.example.coffeeapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class FarmerProducts : AppCompatActivity() {
    private lateinit var edtGrade : EditText
    private lateinit var edtQuantity : EditText
    private lateinit var edtPrice : EditText
    private lateinit var btnPost : Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var userID: String
    private lateinit var binding: ActivityFarmerProductsBinding
    private lateinit var coffeeType: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_products)
        supportActionBar?.hide()
        binding = ActivityFarmerProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roles = listOf("Arabica", "Robusta","Liberica","Excelsa")
        val adapter = ArrayAdapter(this, R.layout.coffee_list_view, roles)
        binding.dropdownField.setAdapter(adapter)


        coffeeType = findViewById(R.id.dropdown_field)

        edtGrade = findViewById(R.id.edt_grade)
        mAuth = FirebaseAuth.getInstance()
        edtQuantity = findViewById(R.id.edt_quantity)
        edtPrice = findViewById(R.id.edt_price)
        btnPost = findViewById(R.id.btn_post)

        //mDbRef = Firebase.database.reference

        btnPost.setOnClickListener(){
            val type = coffeeType.text.toString()
            val grade = edtGrade.text.toString()
            val quantity = edtQuantity.text.toString()
            val price = edtPrice.text.toString()

            addPostToDatabase(type,grade,quantity,price, mAuth.currentUser?.uid!!)
            if (type.isEmpty()) {
                coffeeType.error = "coffee type is required"
                return@setOnClickListener
            } else if (grade.isEmpty()) {
                edtGrade.error = "Grade is required"
                return@setOnClickListener
            }else if (quantity.isEmpty()) {
                edtQuantity.error = "Role is required"
                return@setOnClickListener
            } else if (price.isEmpty()) {
                edtPrice.error = "Price is required"
                return@setOnClickListener
            }
        }
    }

    private fun addPostToDatabase(coffeeType:String,grade: String, quantity: String, price: String, uid: String) {
        user = FirebaseAuth.getInstance().currentUser!!
        mDbRef = FirebaseDatabase.getInstance().getReference("products")
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userID = user.uid
        mDbRef.child("products").push().setValue(Products(coffeeType,grade,quantity,price,uid)).addOnSuccessListener {
            Toast.makeText(this, "Product Posted Successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,Farmer::class.java)
            startActivity(intent)
        }
    }
}