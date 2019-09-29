package org.hotel.service

import org.hotel.service.model.Room
import java.time.LocalDate
import java.util.*

interface RoomService {
    fun registerRoom(roomId: UUID, floor: Int, number: Int)
    fun bookRoom(roomId: UUID, from: LocalDate, numDays: Int)
    fun checkIn(roomId: UUID, checkInDate: LocalDate, numDays: Int)
    fun checkOut(roomId: UUID, checkOutDate: LocalDate)
    fun getRoom(roomId: UUID): Room
}
