package com.example.techpowerhour.data.model

data class User(
    var name: String? = null,
    var imageUrl: String? = null,
) {
    var id: String? = null

    override fun toString(): String {
        return "ID: $id, Name: $name"
    }
}