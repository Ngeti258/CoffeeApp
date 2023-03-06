package com.example.coffeeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mDbRef: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var userID: String


    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignUp = findViewById(R.id.btn_signup)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef = Firebase.database.reference

        btnSignUp.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (email.isEmpty()) {
                edtEmail.error = "Email is required"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                edtPassword.error = "Password is required"
                return@setOnClickListener
            } else {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
    //logic for logging in users
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //code for logging in user
                    //readData(email)
                    user = FirebaseAuth.getInstance().currentUser!!
                    mDbRef = FirebaseDatabase.getInstance().getReference("user")
                    userID = user.uid

                    mDbRef.child(userID).get().addOnSuccessListener {
                        if (it.exists()) {
                            val role = it.child("role").value
                            if (role == "Farmer") {
                                val intent = Intent(this, Farmer::class.java)
                                finish()
                                startActivity(intent)
                            } else if (role == "Customer") {
                                val intent = Intent(this, MainActivity::class.java)
                                finish()
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Toast.makeText(this@Login, "incorrect username or password", Toast.LENGTH_SHORT)
                        .show()

                }
            }
    }
}
