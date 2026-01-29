package com.barberbook.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// API Definitions
interface BarberApi {
    @GET("api/rendezvous")
    suspend fun getAppointments(): List<Appointment>

    @GET("api/config/services")
    suspend fun getServices(): List<ServiceItem>

    @POST("api/config/services")
    suspend fun saveService(@RequestBody service: ServiceItem)

    @DELETE("api/config/services/{id}")
    suspend fun deleteService(@Path("id") id: Long)

    @GET("api/config/slots")
    suspend fun getSlots(): List<SlotItem>

    @POST("api/config/slots")
    suspend fun saveSlot(@RequestBody slot: SlotItem)

    @DELETE("api/config/slots/{id}")
    suspend fun deleteSlot(@Path("id") id: Long)
}

data class Appointment(val clientFirstName: String, val clientName: String, val serviceName: String, val timeSlot: String, val price: Double)
data class ServiceItem(val id: Long = 0, val name: String, val price: Double)
data class SlotItem(val id: Long = 0, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(BarberApi::class.java)

        setContent {
            var currentScreen by remember { mutableStateOf(0) } // 0=Home/CA, 1=Services, 2=Slots
            val scope = rememberCoroutineScope()

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(selected = currentScreen == 0, onClick = { currentScreen = 0 }, icon = { Text("🏕️") }, label = { Text("Stats") })
                        NavigationBarItem(selected = currentScreen == 1, onClick = { currentScreen = 1 }, icon = { Text("🌿") }, label = { Text("Prestations") })
                        NavigationBarItem(selected = currentScreen == 2, onClick = { currentScreen = 2 }, icon = { Text("🗺️") }, label = { Text("Passages") })
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (currentScreen) {
                        0 -> AdminDashboard(api)
                        1 -> ServiceManagement(api)
                        2 -> SlotManagement(api)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(api: BarberApi) {
    var appointments by remember { mutableStateOf(listOf<Appointment>()) }
    LaunchedEffect(Unit) { try { appointments = api.getAppointments() } catch (e: Exception) {} }

    val totalCA = appointments.sumOf { it.price }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("💰 Chiffre d'Affaires", style = MaterialTheme.typography.headlineMedium)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D4C3E), contentColor = Color.White)
        ) {
            Text("$totalCA €", style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(24.dp))
        }
        Text("Dernières réservations :", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(appointments) { rdv ->
                ListItem(headlineContent = { Text("${rdv.clientFirstName} ${rdv.clientName}") }, supportingContent = { Text("${rdv.serviceName} - ${rdv.timeSlot}") })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ServiceManagement(api: BarberApi) {
    var services by remember { mutableStateOf(listOf<ServiceItem>()) }
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun refresh() = scope.launch { try { services = api.getServices() } catch (e: Exception) {} }
    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestion des Formules", style = MaterialTheme.typography.headlineSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(value = newName, onValueChange = { newName = it }, placeholder = { Text("Nom") }, modifier = Modifier.weight(1f))
            TextField(value = newPrice, onValueChange = { newPrice = it }, placeholder = { Text("€") }, modifier = Modifier.width(70.dp))
            IconButton(onClick = { 
                scope.launch { 
                    api.saveService(ServiceItem(name = newName, price = newPrice.toDoubleOrNull() ?: 0.0))
                    newName = ""; newPrice = ""; refresh()
                }
            }) { Icon(Icons.Default.Add, "Add") }
        }
        LazyColumn {
            items(services) { s ->
                ListItem(
                    headlineContent = { Text(s.name) },
                    trailingContent = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${s.price}€")
                            IconButton(onClick = { scope.launch { api.deleteService(s.id); refresh() } }) { Icon(Icons.Default.Delete, "Del") }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SlotManagement(api: BarberApi) {
    var slots by remember { mutableStateOf(listOf<SlotItem>()) }
    var newTime by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun refresh() = scope.launch { try { slots = api.getSlots() } catch (e: Exception) {} }
    LaunchedEffect(Unit) { refresh() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestion des Créneaux", style = MaterialTheme.typography.headlineSmall)
        Row {
            TextField(value = newTime, onValueChange = { newTime = it }, placeholder = { Text("Ex: 09:30") }, modifier = Modifier.weight(1f))
            IconButton(onClick = { scope.launch { api.saveSlot(SlotItem(time = newTime)); newTime = ""; refresh() } }) { Icon(Icons.Default.Add, "Add") }
        }
        LazyColumn {
            items(slots) { s ->
                ListItem(headlineContent = { Text(s.time) }, trailingContent = {IconButton(onClick = { scope.launch { api.deleteSlot(s.id); refresh() } }) { Icon(Icons.Default.Delete, "Del") }})
            }
        }
    }
}
