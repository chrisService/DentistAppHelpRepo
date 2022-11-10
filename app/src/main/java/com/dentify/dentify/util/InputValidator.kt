package com.dentify.dentify.util

import android.text.TextUtils
import android.widget.EditText
import java.util.regex.Pattern

object InputValidator {

    fun EditText.isRequiredFieldNotEmpty(): Boolean {
        return !TextUtils.isEmpty(text.toString())
    }

    fun EditText.isValidEmailAddress(): Boolean {
        return Pattern
            .compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            .matcher(text.toString()).matches()
    }

    fun passwordsMatch(passOne: String, passTwo: String): Boolean {
        return passOne == passTwo
    }

}