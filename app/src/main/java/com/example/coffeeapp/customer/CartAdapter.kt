package com.example.coffeeapp.customer

import DatabaseHelper
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
import com.example.coffeeapp.farmer.Product
import java.text.DecimalFormat

class CartAdapter(private val context: Context, private var productList: List<Product>) :
    RecyclerView.Adapter<CartAdapter.ProductViewHolder>(){

    private var onItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(product: Product) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.coffeeTypeTV.text = product.coffeeType
        holder.coffeeGradeTV.text = " ${product.coffeeGrade}"

        val priceFormat = DecimalFormat("Ksh#,###.## per kilogram")
        val formattedPrice = priceFormat.format(product.price)
        holder.priceTV.text = formattedPrice



        holder.deleteButton.setOnClickListener {
            val db = DatabaseHelper(context)
            db.deleteProduct(product)
            productList.toMutableList().remove(product)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productList.size)
            Toast.makeText(context, "Product removed from cart", Toast.LENGTH_SHORT).show()
        }

        // Load product image using Glide library
        if (product.imageUrl != null && product.imageUrl!!.isNotBlank()) {
            Glide.with(context).load(product.imageUrl).into(holder.productIV)
        } else {
            holder.productIV.setImageResource(R.drawable.baseline_add_photo_alternate_24)
        }
    }

    override fun getItemCount() = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coffeeTypeTV: TextView = itemView.findViewById(R.id.coffeeTypeTV)
        val coffeeGradeTV: TextView = itemView.findViewById(R.id.coffeeGradeTV)
        val priceTV: TextView = itemView.findViewById(R.id.coffeePriceTV)
        val productIV: ImageView = itemView.findViewById(R.id.imageView)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)



    }
}
