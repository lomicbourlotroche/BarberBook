package com.barberbook.model

import jakarta.persistence.*

@Entity
data class TimeSlot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val time: String // Exemple: "09:00"
)
