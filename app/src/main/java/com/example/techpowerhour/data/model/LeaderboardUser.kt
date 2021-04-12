package com.example.techpowerhour.data.model

/**
 * The data class for holding information about a user to display on the Leaderboard.
 * @param id The id of the user
 * @param name The name of the user to display
 * @param points The total points to display.
 */
data class LeaderboardUser (
        val id: String,
        val name: String,
        var points: Int,
) {
    override fun toString(): String {
        return "Name: $name \nPoints: $points"
    }
}