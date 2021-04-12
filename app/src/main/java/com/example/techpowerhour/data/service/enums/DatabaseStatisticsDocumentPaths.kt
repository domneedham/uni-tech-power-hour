package com.example.techpowerhour.data.service.enums

/**
 * Strings used for Document paths in Firestore for fetching points or power_hours.
 * @param path The string of the path.
 */
enum class DatabaseStatisticsDocumentPaths(val path: String) {
    Points("points"),
    PowerHours("power_hours")
}