package com.example.coffeeapp

class Products {
    var type:String? = null
    var grade:String? = null
    var quantity:String? = null
    var price:String? = null
    var uid : String? = null


    constructor(type:String?,grade:String?,quantity:String?,price: String?,uid: String?){
        this.type =  type
        this.grade = grade
        this.quantity = quantity
        this.price = price
        this.uid = uid
    }
}