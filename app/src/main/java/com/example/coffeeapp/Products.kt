package com.example.coffeeapp

class Products {
    var coffeeType:String? = null
    var coffeeGrade:String? = null
    var quantity:Number? = null
    var price:Number? = null
    var uid : String? = null
    var imageUrl: Int? = null

    constructor(){}


    constructor(coffeeType:String?,coffeeGrade:String?,quantity:Number?,price:Number?,uid: String?,imageUrl : Int?){
        this.coffeeType  = coffeeType
        this.coffeeGrade = coffeeGrade
        this.quantity = quantity
        this.price = price
        this.uid = uid
        this.imageUrl = imageUrl
    }
}