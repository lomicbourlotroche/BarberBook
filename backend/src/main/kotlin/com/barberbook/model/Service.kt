package com.barberbook.model

import jakarta.persistence.*

@Entity
data class Service(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val price: Double
)
