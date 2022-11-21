package com.example.strangersapp.models

class User {
    private var uId: String? = null
    var name: String? = null
    var profile: String? = null
    var city: String? = null
    var coins: Long = 0

    constructor() {}
    constructor(uId: String?, name: String?, profile: String?, city: String?, coins: Long) {
        this.uId = uId
        this.name = name
        this.profile = profile
        this.city = city
        this.coins = coins
    }

    fun getuId(): String? {
        return uId
    }

    fun setuId(uId: String?) {
        this.uId = uId
    }
}