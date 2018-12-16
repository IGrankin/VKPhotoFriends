package com.example.igorgrankin.vkphotofriends.models

class FriendModel {
    var name: String
    var surname: String
    var city: String?
    var avatar: String?
    var isOnline: Boolean
    var id: String

    constructor(name: String, surname:String, city: String?, avatar: String?, isOnline: Boolean, id: String) {
        this.name = name
        this.surname = surname
        this.city = city
        this.avatar = avatar
        this.isOnline = isOnline
        this.id = id
    }
}