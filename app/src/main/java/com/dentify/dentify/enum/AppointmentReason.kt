package com.dentify.dentify.enum

enum class AppointmentReason(val value: Int) {
    None(0),
    Toothache(1),
    DamagedTooth(2),
    GumsMucosMembranes(3),
    WisdomTeeth(4),
    JawProblem(5),
    FillingCrown(6),
    ToothSensitivity(7),
    AestheticDentistry(8),
    Other(9);

    companion object {
        fun getByValue(value: Int) = AppointmentReason.values().find { it.value == value }
    }
}
