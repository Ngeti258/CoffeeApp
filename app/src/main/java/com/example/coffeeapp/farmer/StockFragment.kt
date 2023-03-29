package com.example.coffeeapp.farmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.coffeeapp.R

class StockFragment : Fragment() {
    private  lateinit var imageView: ImageView
    private lateinit var coffeeTypeTextView: TextView
    private lateinit var coffeeGradeTextView: TextView
    private  lateinit var coffeeQuantityEditText: EditText
    private  lateinit var coffeePriceEditText: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock, container, false)
        imageView = view.findViewById(R.id.imageView)
        coffeeTypeTextView = view.findViewById(R.id.coffee_type)
        coffeeGradeTextView = view.findViewById(R.id.coffee_grade)
        coffeeQuantityEditText = view.findViewById(R.id.coffee_quantity)
        coffeePriceEditText = view.findViewById(R.id.coffee_price)

        val bundle = arguments
        if (bundle != null) {
            val coffeeType = bundle.getString("coffeeType")
            val coffeeGrade = bundle.getString("coffeeGrade")
            val coffeeQuantity = bundle.getString("coffeeQuantity")
            val coffeePrice = bundle.getString("coffeePrice")

            imageView.setImageResource(R.drawable.arabica)
            coffeeTypeTextView.text = coffeeType
            coffeeGradeTextView.text = coffeeGrade
            coffeeQuantityEditText.setText(coffeeQuantity)
            coffeePriceEditText.setText(coffeePrice)
        }
        return view
    }
}