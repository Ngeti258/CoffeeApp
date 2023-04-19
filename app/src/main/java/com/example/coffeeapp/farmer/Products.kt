package com.example.coffeeapp.farmer

import com.google.firebase.auth.FirebaseAuth

class Products {
    var coffeeType:String? = null
    var coffeeGrade:String? = null
    var quantity:Number? = null
    var price:Number? = null
    var uid : String? = null
    var name : String? = FirebaseAuth.getInstance().currentUser?.displayName
    private var imageUrl: Int? = null
    private var productId : String? = null

    constructor(){}


    constructor(coffeeType:String?,coffeeGrade:String?,quantity:Number?,price:Number?,name:String?,uid: String?,imageUrl : Int?,productId : String?){
        this.coffeeType  = coffeeType
        this.coffeeGrade = coffeeGrade
        this.quantity = quantity
        this.name = name
        this.price = price
        this.uid = uid
        this.imageUrl = imageUrl
        this.productId = productId
    }
}