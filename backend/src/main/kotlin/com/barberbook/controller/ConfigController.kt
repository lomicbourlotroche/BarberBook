package com.barberbook.controller

import com.barberbook.model.Service
import com.barberbook.model.TimeSlot
import com.barberbook.repository.ServiceRepository
import com.barberbook.repository.TimeSlotRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = ["*"])
class ConfigController(
    val serviceRepository: ServiceRepository,
    val slotRepository: TimeSlotRepository
) {
    @GetMapping("/services")
    fun getServices(): List<Service> = serviceRepository.findAll()

    @PostMapping("/services")
    fun saveService(@RequestBody service: Service) = serviceRepository.save(service)
    
    @DeleteMapping("/services/{id}")
    fun deleteService(@PathVariable id: Long) = serviceRepository.deleteById(id)

    @GetMapping("/slots")
    fun getSlots(): List<TimeSlot> = slotRepository.findAll()

    @PostMapping("/slots")
    fun saveSlot(@RequestBody slot: TimeSlot) = slotRepository.save(slot)
    
    @DeleteMapping("/slots/{id}")
    fun deleteSlot(@PathVariable id: Long) = slotRepository.deleteById(id)
}
