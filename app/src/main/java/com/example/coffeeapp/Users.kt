package com.example.coffeeapp

class Users {
    var name:String? = null
    var email:String? = null
    var role:String? = null
    var uid:String? = null

    constructor(){}

    constructor(name:String?,email:String?,role: String?,uid:String?){
        this.name = name
        this.email = email
        this.role = role
        this.uid = uid
    }

//    fun getName(): String? {
//        return name
//    }
//
//    fun getEmail(): String? {
//        return email
//    }

//    fun getRole(): String? {
//        return role
//    }
}