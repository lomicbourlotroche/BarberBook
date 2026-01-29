package com.barberbook.controller

import com.barberbook.model.RendezVous
import com.barberbook.repository.RendezVousRepository
import org.springframework.web.bind.annotation.*
import org.springframework.transaction.annotation.Transactional

// ANALOGIE : Le Controller est comme le "Guichet" où le client vient poser ses questions ou son formulaire.
@RestController
@RequestMapping("/api/rendezvous")
@CrossOrigin(origins = ["*"]) // Autorise React et Android à se connecter
class RendezVousController(val repository: RendezVousRepository) {

    // Récupérer tous les RDV (utile pour le Barbier sur son Mobile)
    @GetMapping
    fun getAll(): List<RendezVous> = repository.findAll()

    // Ajouter un nouveau RDV (depuis le Site Web)
    @PostMapping
    fun create(@RequestBody rdv: RendezVous): RendezVous = repository.save(rdv)

    // Supprimer un RDV avec son numéro de téléphone (Sécurité demandée)
    @DeleteMapping("/{phoneNumber}")
    @Transactional
    fun deleteByPhone(@PathVariable phoneNumber: String) {
        repository.deleteByPhoneNumber(phoneNumber)
    }
}
