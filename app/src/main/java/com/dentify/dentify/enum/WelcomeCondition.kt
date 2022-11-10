package com.dentify.dentify.enum

enum class WelcomeCondition(val value: Int) {
    None(0),
    Back(1),
    Welcome(2);


    companion object {
        fun getByValue(value: Int) = WelcomeCondition.values().find { it.value == value }
    }
}
