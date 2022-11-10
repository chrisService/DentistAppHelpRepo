package com.dentify.dentify.enum

import com.dentify.dentify.util.Constants

enum class TwilioRoomStatus(val value: Int, val valueString: String) {
    Created(1, Constants.ROOM_CREATED),
    NotCreated(0, Constants.ROOM_NOT_CREATED);

    companion object {
        fun getByValue(value: Int) = values().find { it.value == value }
    }
}
