package com.example.coffeeapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeapp.customer.Customer
import com.example.coffeeapp.databinding.ActivitySignupBinding
import com.example.coffeeapp.farmer.Farmer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Signup : AppCompatActivity() {

    private lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtPassword: EditText
    lateinit var edtPasswordConfirmation: EditText
    lateinit var btnSignUp: Button
    private lateinit var loginText: TextView

    private lateinit var binding: ActivitySignupBinding
    private lateinit var userRole:AutoCompleteTextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = listOf("Farmer", "Customer")
        val adapter = ArrayAdapter(this, R.layout.role_list_view, role)
        binding.dropdownField.setAdapter(adapter)


        userRole = findViewById(R.id.dropdown_field)
        loginText = findViewById(R.id.text_login)
        edtName = findViewById(R.id.edt_Name)
        edtEmail = findViewById(R.id.edt_Email)
        edtPassword = findViewById(R.id.edt_Password)
        edtPasswordConfirmation = findViewById(R.id.edt_PasswordConfirmation)
        btnSignUp = findViewById(R.id.btn_Signup)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef = Firebase.database.reference

        loginText.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val role = userRole.text.toString()
            val password = edtPassword.text.toString()
            val password_confirmation = edtPasswordConfirmation.text.toString()

            if (name.isEmpty()) {
                edtName.error = "name is required"
                return@setOnClickListener
            } else if (email.isEmpty()) {
                edtEmail.error = "Email is required"
                return@setOnClickListener
            }else if (role.isEmpty()) {
                userRole.error = "Role is required"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                edtPassword.error = "Password is required"
                return@setOnClickListener
            } else if (password == password_confirmation) {
                signup(name, email, role,password)
            } else if (password_confirmation.isEmpty()) {
                edtPasswordConfirmation.error = "Confirm your password"
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signup(name: String, email: String,role: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //code for jumping to home activity
                    addUserToDatabase(name, email,role, mAuth.currentUser?.uid!!)
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
                                val intent = Intent(this, Customer::class.java)
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
                    Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()

                }
            }
    }
    private fun addUserToDatabase(name: String, email: String, role: String,uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(Users(name, email, role, uid))
    }
}





