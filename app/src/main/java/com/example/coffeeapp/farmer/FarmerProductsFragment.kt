package com.example.coffeeapp.farmer
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.coffeeapp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class FarmerProductsFragment : Fragment() {
    private lateinit var coffeeTypeDropdown: AutoCompleteTextView
    private lateinit var coffeeGradeEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var imageView: ImageView
    private lateinit var imageButton: ImageButton
    private val REQUEST_CODE_IMAGE_PICK = 100

    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_farmer_products, container, false)
        val myProducts : Button = view.findViewById(R.id.my_products)
        myProducts.setOnClickListener {
            val fragment = ProductsFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout,fragment).commit()
        }

        // Initialize views
        coffeeTypeDropdown = view.findViewById(R.id.dropdown_field)
        storage = FirebaseStorage.getInstance()

        val coffeeTypeDropdownLayout = view.findViewById<TextInputLayout>(R.id.dropdown_field2)
        coffeeGradeEditText = view.findViewById(R.id.edt_grade)
        quantityEditText = view.findViewById(R.id.edt_quantity)
        priceEditText = view.findViewById(R.id.edt_price)
        imageView = view.findViewById(R.id.imageView)
        imageButton = view.findViewById(R.id.imageButton)

        val postButton = view.findViewById<Button>(R.id.btn_post)
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val imageRef = storageRef.child("images/image.jpg")


        // Get user ID
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val name = auth.currentUser?.displayName


        // Set up Coffee type dropdown
        val coffeeTypes = listOf("Arabica", "Robusta", "Liberica", "Excelsa")
        val adapter = ArrayAdapter(requireContext(), R.layout.coffee_list_view, coffeeTypes)
        coffeeTypeDropdown.setAdapter(adapter)
        coffeeTypeDropdownLayout.setEndIconOnClickListener {
            coffeeTypeDropdown.setText("")
        }
        imageButton.setOnClickListener {
            openImageChooser()
        }


// Set up Post button
        postButton.setOnClickListener {
            // Get input values
            val coffeeType = coffeeTypeDropdown.text.toString()
            val coffeeGrade = coffeeGradeEditText.text.toString()
            val quantity = quantityEditText.text.toString().toDoubleOrNull()
            val price = priceEditText.text.toString().toDoubleOrNull()
            val name= userId?.let { it1 ->

                if (name != null) {
                    auth.currentUser?.displayName?.let { it2 ->
                        database.reference.child("user").child(it1).get()
                    }
                }
            }

            val userId = auth.currentUser?.uid
            if (coffeeType.isBlank()){
                coffeeTypeDropdownLayout.error = "Please select a Coffee type"
                return@setOnClickListener
            } else {
                coffeeTypeDropdownLayout.error = null
            }
            if (coffeeGrade.isBlank()) {
                coffeeGradeEditText.error = "Please enter a Coffee Grade"
                return@setOnClickListener
            } else {
                coffeeGradeEditText.error = null
            }
            if ((quantity == null) || (quantity <= 0)) {
                quantityEditText.error = "Please enter a valid Quantity"
                return@setOnClickListener
            } else {
                quantityEditText.error = null
            }
            if ((price == null) || (price <= 0)) {
                priceEditText.error = "Please enter a valid Price"
                return@setOnClickListener
            } else {
                priceEditText.error = null
            }

            // Create a new product object
            val product = Product(coffeeType, coffeeGrade, quantity,price,userId)

            if (imageUri != null) {
                val storageRef = storage.reference
                val imageRef = storageRef.child("product_images/${product.hashCode()}")
                val uploadTask = imageRef.putFile(imageUri!!)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        // Add download URL to product object
                        product.imageUrl = downloadUri.toString()
                        // Add product to Firebase Realtime Database with product ID as a property
                        val productsRef = database.getReference("products")
                        val productId = productsRef.push().key
                        productId?.let {
                            product.productId = it
                            product.name = name.toString()
                            productsRef.child(productId).setValue(product)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        activity?.applicationContext,
                                        "Product added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val fragment = ProductsFragment()
                                    val transaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.frame_layout, fragment).commit()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        getActivity()?.applicationContext,
                                        "Error adding product: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            getActivity()?.applicationContext,
                            "Error uploading image: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "image missing,add image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }
}
    data class Product(
        val coffeeType: String? = null,
        val coffeeGrade: String? = null,
        val quantity: Double? = 0.0,
        val price: Double? = 0.0,
        val userId: String? = null,
        var name:  String?=null,
        var cartProductId: String? = null,
        var imageUrl: String? = null,
        var productId: String? = null,
        var farmerId: String? = null,
        var orderId: String? = null
    ) {
    }



