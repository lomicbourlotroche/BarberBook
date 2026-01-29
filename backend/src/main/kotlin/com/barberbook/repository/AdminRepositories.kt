package com.barberbook.repository

import com.barberbook.model.Service
import com.barberbook.model.TimeSlot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceRepository : JpaRepository<Service, Long>

@Repository
interface TimeSlotRepository : JpaRepository<TimeSlot, Long>
