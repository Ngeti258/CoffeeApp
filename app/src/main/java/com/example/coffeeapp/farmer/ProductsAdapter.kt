package com.example.coffeeapp.farmer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeapp.R
import java.text.DecimalFormat

class ProductAdapter(private val context: Context, var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

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
        val view = LayoutInflater.from(context).inflate(R.layout.products_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.coffeeTypeTV.text = product.coffeeType
        holder.coffeeGradeTV.text = " ${product.coffeeGrade}"

        val priceFormat = DecimalFormat("Ksh#,###.## per kilogram")
        val formattedPrice = priceFormat.format(product.price)
        holder.priceTV.text = formattedPrice

        holder.productIV.setOnClickListener() {
            onItemClickListener.onItemClick(product)

        }

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
    }
}