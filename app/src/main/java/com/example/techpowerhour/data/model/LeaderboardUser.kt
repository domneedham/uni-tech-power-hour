package com.example.techpowerhour.data.model

data class LeaderboardUser (
        val id: String,
        val name: String,
        var points: Int,
) {
    override fun toString(): String {
        return "Name: $name \nPoints: $points"
    }
}