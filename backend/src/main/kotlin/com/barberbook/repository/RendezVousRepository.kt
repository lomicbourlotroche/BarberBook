package com.barberbook.repository

import com.barberbook.model.RendezVous
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// ANALOGIE : Le Repository est comme le "Serveur" qui va chercher les fiches dans le classeur.
@Repository
interface RendezVousRepository : JpaRepository<RendezVous, Long> {
    // Permet de trouver les RDV par numéro de téléphone pour la suppression
    fun findByPhoneNumber(phoneNumber: String): List<RendezVous>
    
    // Permet de supprimer par numéro de téléphone
    fun deleteByPhoneNumber(phoneNumber: String)
}
