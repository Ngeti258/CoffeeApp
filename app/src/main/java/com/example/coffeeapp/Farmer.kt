package com.example.coffeeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class Farmer : AppCompatActivity() {
    lateinit var btnNewProduct:Button
    lateinit var btnViewOrders:Button
    lateinit var btnViewProfile:Button
    lateinit var btnLogout:Button
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer)

        btnNewProduct = findViewById(R.id.btn_new_product)
        btnViewOrders = findViewById(R.id.btn_view_orders)
        btnViewProfile = findViewById(R.id.btn_view_profile)
        mAuth = FirebaseAuth.getInstance()

        btnLogout = findViewById(R.id.btn_logout)

        btnLogout.setOnClickListener(){
            mAuth.signOut()
            val intent = Intent(this@Farmer,Login::class.java)
            finish()
            startActivity(intent)
        }
        btnViewProfile.setOnClickListener(){
            val intent = Intent(this@Farmer,Profile::class.java)
            startActivity(intent)
        }
        btnViewOrders.setOnClickListener(){
            val intent = Intent(this@Farmer,History::class.java)
            startActivity(intent)
        }
        btnNewProduct.setOnClickListener(){
            val intent = Intent(this@Farmer,FarmerProducts::class.java)
            startActivity(intent)
        }
    }
}