package com.example.techpowerhour

import com.example.techpowerhour.data.repository.PowerHourRepository

object Repositories {
    val powerHour by lazy { PowerHourRepository() }
}