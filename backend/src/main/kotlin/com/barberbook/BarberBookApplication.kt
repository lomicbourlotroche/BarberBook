package com.barberbook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// ANALOGIE : C'est le bouton "ON" de toute la machine (le Cerveau).
@SpringBootApplication
class BarberBookApplication

fun main(args: Array<String>) {
    runApplication<BarberBookApplication>(*args)
}
