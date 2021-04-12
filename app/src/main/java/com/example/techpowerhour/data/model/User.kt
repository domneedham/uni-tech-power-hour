package com.example.techpowerhour.data.model

/**
 * The basic class to store information about a user.
 * @param name The name of the user
 * @param imageUrl The url to the image of the user. **Not currently used**.
 */
data class User(
    var name: String? = null,
    var imageUrl: String? = null,
) {
    /**
     * The id of the user in persistent storage.
     */
    var id: String? = null

    override fun toString(): String {
        return "ID: $id, Name: $name"
    }
}
