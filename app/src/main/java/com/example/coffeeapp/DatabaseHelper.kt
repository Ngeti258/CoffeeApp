package com.example.coffeeapp

import android.content.Context
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DatabaseHelper(context: Context) {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val databaseII = FirebaseDatabase.getInstance().reference.child("carts").child(currentUser?.uid ?: "")
    private val databaseIII = FirebaseDatabase.getInstance().reference.child("history")




    fun deleteCartItem(product: Product) {
        val cartItemQuery: Query = databaseII.orderByChild("cartProductId").equalTo(product.orderId)

        cartItemQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartItemSnapshot in snapshot.children) {
                    cartItemSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun deleteOrderHistoryItem(product: Product) {

        product.farmerId?.let {
            databaseIII.orderByChild("productId").equalTo(product.productId)
        }?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartItemSnapshot in snapshot.children) {
                    cartItemSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}