package org.hotel.service.model

import java.util.*

data class Room(val roomId: UUID, val floor: Int, val number: Int, val occupied: Boolean)