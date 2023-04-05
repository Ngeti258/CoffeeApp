package com.example.coffeeapp

import android.content.Context
import com.example.coffeeapp.farmer.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context

class DatabaseHelper(context: Context) {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val databaseII = FirebaseDatabase.getInstance().reference.child("carts").child(currentUser?.uid ?: "")


    fun deleteCartItem(product: Product) {
        val cartItemQuery: Query = databaseII.orderByChild("productId").equalTo(product.productId)

        cartItemQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartItemSnapshot in snapshot.children) {
                    cartItemSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(context, "Failed to delete product from cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    fun updateCartTotal() {
//        database.child("cartTotal").setValue(calculateCartTotal())
//    }
//
//    private fun calculateCartTotal(): Double {
//        var total = 0.0
//        for (cartItem in cartItems) {
//            total += cartItem.price * cartItem.quantity
//        }
//        return total
//    }
}
