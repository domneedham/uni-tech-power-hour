package com.example.techpowerhour.data.service.enums

/**
 * Strings used for collection paths in Firestore.
 * @param type The string value.
 */
enum class PowerHourDatabaseDateType(val type: String) {
    Day("days"),
    Week("weeks"),
    Month("months")
}