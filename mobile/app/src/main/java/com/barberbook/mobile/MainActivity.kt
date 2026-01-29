package com.barberbook.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// ANALOGIE : Le "Client" Retrofit est le facteur qui va chercher le courrier au Cerveau (Backend).
interface BarberApi {
    @GET("api/rendezvous")
    suspend fun getAppointments(): List<Appointment>
}

data class Appointment(
    val clientFirstName: String,
    val clientName: String,
    val serviceName: String,
    val timeSlot: String,
    val price: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration de la connexion (10.0.2.2 = ton ordi depuis l'émulateur)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(BarberApi::class.java)

        setContent {
            var appointments by remember { mutableStateOf(listOf<Appointment>()) }
            
            // ANALOGIE : LaunchedEffect c'est comme demander au serveur de vérifier les nouveautés.
            LaunchedEffect(Unit) {
                try { appointments = api.getAppointments() } catch (e: Exception) {}
            }

            // ANALOGIE : Chiffre d'Affaires calculé avec .sumOf (comme une calculette automatique).
            val totalCA = appointments.sumOf { it.price }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("💈 BarberBook - QG Barbier", style = MaterialTheme.typography.headlineMedium)
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💰 Chiffre d'Affaires Total", style = MaterialTheme.typography.titleLarge)
                        Text("$totalCA €", style = MaterialTheme.typography.headlineLarge, color = Color.DarkGray)
                    }
                }

                Text("📅 Liste des RDV :", style = MaterialTheme.typography.titleMedium)
                
                LazyColumn {
                    items(appointments) { rdv ->
                        ListItem(
                            headlineContent = { Text("${rdv.clientFirstName} ${rdv.clientName}") },
                            supportingContent = { Text("${rdv.serviceName} à ${rdv.timeSlot}") },
                            trailingContent = { Text("${rdv.price} €") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
