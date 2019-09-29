package org.hotel.service

import org.assertj.core.api.Assertions.assertThat
import org.hotel.service.model.Room
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.*

class RoomServiceTests {
    private val floor: Int = 3
    private val roomNumber: Int = 23
    private val today = LocalDate.now()
    private lateinit var roomId: UUID

    private lateinit var roomService: RoomService

    @BeforeEach
    fun setUp() {
        roomService = RoomServiceImpl()
        roomId = UUID.randomUUID()
    }

    @Test
    fun `Given room not registered, then no command allowed apart from registration`() {
        assertThrows<IllegalStateException> { roomService.bookRoom(roomId, LocalDate.now(), 1) }
        assertThrows<IllegalStateException> { roomService.checkIn(roomId, today, 1) }
        assertThrows<IllegalStateException> { roomService.checkOut(roomId, today) }
    }

    @Test
    fun `Given room not registered, then registration succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        assertThat(roomService.getRoom(roomId)).isEqualTo(Room(roomId, floor, roomNumber, false))
    }

    @Test
    fun `Given room registered, then second registration and check out fail`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        assertThrows<IllegalStateException> { roomService.registerRoom(roomId, 4, 3) }
        assertThrows<IllegalStateException> { roomService.checkOut(roomId, today) }
    }

    @Test
    fun `Given room registered, then check in succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(true)
    }

    @Test
    fun `Given room registered, then booking succeeds for free period`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.bookRoom(roomId, today, 1)
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(false)
    }

    //    @Test
    fun `Given room registered, then booking fails for already booked period`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.bookRoom(roomId, today, 2)
        assertThrows<RoomOccupiedException> { roomService.bookRoom(roomId, today.plusDays(1), 1) }
    }

    //    @Test
    fun `Given room registered and someone checked in, then a new check in fails and register room fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        assertThrows<IllegalStateException> { roomService.registerRoom(roomId, floor, roomNumber) }
        assertThrows<RoomOccupiedException> { roomService.checkIn(roomId, today, 2) }
    }

    //    @Test
    fun `Given room registered and someone checked in, then booking in that period fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        assertThrows<RoomOccupiedException> { roomService.bookRoom(roomId, today, 2) }
    }

    @Test
    fun `Given room registered and someone checked in, then booking in another period succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.bookRoom(roomId, today.plusDays(1), 1)
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(true)
    }

    @Test
    fun `Given room registered and someone checked in, then booking in the past fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        assertThrows<DateInThePastException> { roomService.bookRoom(roomId, today.minusDays(1), 1) }
    }

    @Test
    fun `Given room registered and someone checked in, then check out later succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today.plusDays(1))
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(false)
    }

    @Test
    fun `Given room registered and someone checked in, then check out earlier than check in date fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today.plusDays(1), 1)
        assertThrows<DateInThePastException> { roomService.checkOut(roomId, today.minusDays(1)) }
    }

    @Test
    fun `Given room registered and somebody checked in and out, then check in succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today)
        roomService.checkIn(roomId, today, 1)
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(true)
    }

    @Test
    fun `Given room registered and somebody checked in and out, then booking succeeds`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today)
        roomService.bookRoom(roomId, today, 1)
        assertThat(roomService.getRoom(roomId).occupied).isEqualTo(false)
    }

    @Test
    fun `Given room registered and somebody checked in and out, then booking in the past fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today)
        assertThrows<DateInThePastException> { roomService.bookRoom(roomId, today.minusDays(1), 1) }
    }

    @Test
    fun `Given room registered and somebody checked in and out, then check in the past fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today)
        assertThrows<DateInThePastException> { roomService.checkIn(roomId, today.minusDays(1), 1) }
    }

    @Test
    fun `Given room registered and somebody checked in and out, then register room again fails`() {
        roomService.registerRoom(roomId, floor, roomNumber)
        roomService.checkIn(roomId, today, 1)
        roomService.checkOut(roomId, today)
        assertThrows<IllegalStateException> { roomService.registerRoom(roomId, floor, roomNumber) }
    }
}