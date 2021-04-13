package com.example.techpowerhour.data.model.enums

import com.example.techpowerhour.R

/**
 * An Enum to represent the different workouts available in the app.
 * @param difficulty The determined difficulty of the workout.
 * @param displayName The name to use in the UI so that it can be translated by Android.
 */
enum class PowerHourType(val difficulty: Int, val displayName: Int) {
    Walk(1, R.string.ph_type_walk),
    Run(3, R.string.ph_type_run),
    Cycle(3, R.string.ph_type_cycle),
    Weights(3, R.string.ph_type_weights),
    Yoga(2, R.string.ph_type_yoga),
    Pilates(2, R.string.ph_type_pilates),
    Dance(2, R.string.ph_type_dance);
}
