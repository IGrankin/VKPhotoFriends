package com.example.igorgrankin.vkphotofriends.models

import java.sql.Timestamp

class PhotoModel {
    var owner: FriendModel
    var timestamp: String
    var image: String
    var likes: Int
    var comments: Int

    constructor(owner: FriendModel, timestamp: String, image: String, likes: Int, comments: Int) {
        this.owner = owner
        this.timestamp = timestamp
        this.image = image
        this.likes = likes
        this.comments = comments
    }

}