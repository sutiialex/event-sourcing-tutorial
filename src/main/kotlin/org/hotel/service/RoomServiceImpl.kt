package org.hotel.service

import org.hotel.service.model.Room
import java.time.LocalDate
import java.util.*

class RoomServiceImpl : RoomService {
    private var room: Room? = null

    override fun registerRoom(roomId: UUID, floor: Int, number: Int) {
        room = if (room != null) throw IllegalStateException() else Room(roomId, floor, number, false)
    }

    override fun bookRoom(roomId: UUID, from: LocalDate, numDays: Int) {
        if (room == null)
            throw IllegalStateException()
    }

    override fun checkIn(roomId: UUID, checkInDate: LocalDate, numDays: Int) {
        if (room != null) {
            room = room!!.copy(occupied = true)
        } else
            throw IllegalStateException()
    }

    override fun checkOut(roomId: UUID, checkOutDate: LocalDate) {
        throw IllegalStateException()
    }

    override fun getRoom(roomId: UUID): Room {
        return room ?: throw Exception("Room not found")
    }
}
