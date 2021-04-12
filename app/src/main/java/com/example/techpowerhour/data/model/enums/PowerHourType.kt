package com.example.techpowerhour.data.model.enums

/**
 * An Enum to represent the different workouts available in the app.
 * @param difficulty The determined difficulty of the workout.
 */
enum class PowerHourType(val difficulty: Int) {
    Walk(1),
    Run(3),
    Cycle(3),
    Weights(3),
    Yoga(2),
    Pilates(2),
    Dance(2)
}