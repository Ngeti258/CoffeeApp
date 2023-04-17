package com.example.coffeeapp.farmer

data class Orders(
    val coffeeType: String? = null,
    val coffeeGrade: String? = null,
    val quantity: Double? = 0.0,
    val price: Double? = 0.0,
    val userId: String? = null,
    var imageUrl: String? = null,
    var productId: String? = null,
    var farmerId: String? = null,
    var orderId: String? = null,

    )
