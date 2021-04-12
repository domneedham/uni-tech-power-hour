package com.example.techpowerhour.data.service.enums

/**
 * Strings used for Collection paths in Firestore to the main collections.
 * @param path The string of the path.
 */
enum class DatabaseCollectionPaths(val path: String) {
    PowerHour("power_hours"),
    Leaderboard("leaderboard"),
    Statistics("statistics"),
    User("users")
}