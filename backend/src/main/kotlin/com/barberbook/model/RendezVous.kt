package com.barberbook.model

import jakarta.persistence.*
import java.time.LocalDate

// ANALOGIE : L'entité est comme une "Fiche Client" dans notre classeur de cuisine.
@Entity
@Table(name = "rendez_vous")
data class RendezVous(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val clientName: String,
    val clientFirstName: String,
    val phoneNumber: String, // Utilisé pour supprimer son propre RDV
    val serviceName: String, // Exemple: "Coupe + Barbe"
    val date: LocalDate,
    val timeSlot: String,    // Exemple: "14:00 - 14:30"
    val price: Double
)
