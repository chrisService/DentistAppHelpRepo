package com.dentify.dentify.enum

import com.dentify.dentify.util.Constants

enum class AppointmentStatus(val value: Int, val valueString: String) {
    Active(1, Constants.APPOINTMENT_ACTIVE),
    Done(2, Constants.APPOINTMENT_DONE),
    Canceled(3, Constants.APPOINTMENT_CANCELED),
    Requested(4, Constants.APPOINTMENT_REQUESTED),
    RequestDenied(5, Constants.APPOINTMENT_REQUEST_DENIED);

    companion object {
        fun getByValue(value: Int) = values().find { it.value == value }
    }
}
