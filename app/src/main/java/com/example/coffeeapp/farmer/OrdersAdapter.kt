package com.example.coffeeapp.farmer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class OrdersAdapter(
    private val context: Context,
    private var OrdersList: List<OrdersFragment.Orders>
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.orders_layout, parent, false)
        return OrdersViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {

        val order = OrdersList[position]

        //holder.customerNameTV.text = order.farmerId
        holder.coffeeTypeTV.text = order.coffeeType
        holder.coffeeGradeTV.text = " ${order.coffeeGrade}"

        val priceFormat = DecimalFormat("Ksh#,###.## per kilogram")
        val formattedPrice = priceFormat.format(order.price)
        holder.priceTV.text = formattedPrice

        // Load product image using Glide library
        if (order.imageUrl != null && order.imageUrl!!.isNotBlank()) {
            Glide.with(context).load(order.imageUrl).into(holder.coffeeIV)
        } else {
            holder.coffeeIV.setImageResource(R.drawable.baseline_add_photo_alternate_24)
        }

        holder.processButton.setOnClickListener(){
            val orderItem = OrderItem(
                order.coffeeType,
                order.coffeeGrade,
                order.price,
                order.quantity,
                order.imageUrl,
                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                order.productId,
                order.userId,


                )

            val historyRef = FirebaseDatabase.getInstance().getReference("history")
            val cartProductId = historyRef.push().key
            orderItem.orderId=cartProductId
            FirebaseAuth.getInstance().currentUser?.uid?.let { it3 ->
                order.productId?.let { it1 ->
                    if (cartProductId != null) {
                        historyRef.child(cartProductId).setValue(orderItem)
                            .addOnSuccessListener {
                                // Remove the item from the local productList and notify the adapter
                                OrdersList = OrdersList.filterNot { it.orderId == order.orderId }
                                notifyDataSetChanged()

                                // Delete the processed order from the "orders" node in the database
                                val databaseRef = FirebaseDatabase.getInstance().getReference("orders")
                                databaseRef.child(order.orderId ?: "").removeValue()

                                Toast.makeText(
                                    context,
                                    "Order processed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to process order: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                    }
                }
            }
        }
    }



    override fun getItemCount() = OrdersList.size

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        //val customerNameTV: TextView = itemView.findViewById(R.id.customerNameTV)
        val coffeeIV:ImageView = itemView.findViewById(R.id.imageView)
        val coffeeGradeTV: TextView = itemView.findViewById(R.id.coffeeGradeTV)
        val priceTV: TextView = itemView.findViewById(R.id.coffeePriceTV)
        val coffeeTypeTV: TextView = itemView.findViewById(R.id.coffeeTypeTV)
        val processButton: Button = itemView.findViewById(R.id.process_order)
    }
}

class OrderItem(
    coffeeType: String?,
    coffeeGrade: String?,
    price: Double?,
    quantity: Double?,
    imageUrl: String?,
    s: String,
    productId: String?,
    farmerId: String?
) {
    val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    val farmerId: String? = farmerId
    val coffeeGrade: String? = coffeeGrade
    val price : Double? = price
    val imageUrl : String? = imageUrl
    val coffeeType: String? = coffeeType
    val productId : String? = productId
    var orderId : String? = null // add cartProductId as a property
}