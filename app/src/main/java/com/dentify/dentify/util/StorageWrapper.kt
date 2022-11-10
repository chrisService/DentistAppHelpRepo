package com.dentify.dentify.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dentify.dentify.apiModel.model.*
import com.dentify.dentify.apiModel.response.GetPatientsProfileResponse
import java.io.File
import java.util.*

object StorageWrapper {

    private lateinit var sharedPref: SharedPreferences

    fun initialize(context: Context) {
        Log.d("Storage", "observer test: initialize")
        sharedPref = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun saveJsonData(fileName: String, jsonData: String, context: Context) {
        val rootFolder = context.filesDir
        val mainFolder = File(rootFolder, Constants.SAVED_JSON_DATA_NAME)
        if (!mainFolder.exists()) {
            mainFolder.mkdirs()
        }

        val file = File(mainFolder, fileName)
        file.writeText(jsonData)
    }

    private fun readJsonData(fileName: String, context: Context): String? {
        var jsonData: String? = null
        val rootFolder = context.filesDir
        val mainFolder = File(rootFolder, Constants.SAVED_JSON_DATA_NAME)
        if (!mainFolder.exists()) {
            mainFolder.mkdirs()
        }
        val file = File(mainFolder, fileName)
        if (file.exists()) {
            jsonData = file.readText()
        }
        return jsonData
    }

    var welcomeCondition : Int
        get() = sharedPref.getInt(Constants.WELCOME_CONDITION, 0)
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(Constants.WELCOME_CONDITION, value)
            editor.apply()
        }

    var reviewCondition : Int
        get() = sharedPref.getInt(Constants.REVIEW_CONDITION, 0)
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(Constants.REVIEW_CONDITION, value)
            editor.apply()
        }

    var selectedAppointmentReason : Int
        get() = sharedPref.getInt(Constants.SELECTED_REASON, 0)
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(Constants.SELECTED_REASON, value)
            editor.apply()
        }

    var splashText : Int
        get() = sharedPref.getInt(Constants.SPLASH_TEXT, Constants.SPLASH_TEXT_1)
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt(Constants.SPLASH_TEXT, value)
            editor.apply()
        }


    //save access token you get by Login Api Call or RefreshToken Api Call(use this for header Authorization)
    var accessToken: String?
        get() = sharedPref.getString(Constants.ACCESS_TOKEN, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.ACCESS_TOKEN, value)
            editor.apply()
        }


    //save firebaseCloudMessages token,app generates this after app install(use to subscribe device for push notifications)
    var fcmTokken: String?
        get() = sharedPref.getString(Constants.FCM_TOKEN, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.FCM_TOKEN, value)
            editor.apply()
        }


    //save refresh token(use in RefreshToken request to get new access token)
    var refreshToken: String?
        get() = sharedPref.getString(Constants.REFRESH_TOKEN, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.REFRESH_TOKEN, value)
            editor.apply()
        }


    //save BankID order reference number you get by Bank/Auth Api call(use it to authenticate with BankID)
    var orderRef: String?
        get() = sharedPref.getString(Constants.ORDER_REF, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.ORDER_REF, value)
            editor.apply()
        }


    //save invitationID that yoy get from deepLink(use this to get user Email by Api Call, use in SignUp Request)
    var invitationId: String?
        get() = sharedPref.getString(Constants.INVITATION_ID, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.INVITATION_ID, value)
            editor.apply()
        }


    //save the email user enters to get password reset instructions(use this for resend Api Call in CheckYourEmailFragment)
    var resetPasswordEmail: String?
        get() = sharedPref.getString(Constants.RESSET_PASSWORD_EMAIL, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.RESSET_PASSWORD_EMAIL, value)
            editor.apply()
        }


    //save patient profile picture
    var profilePictureUri : String?
        get() = sharedPref.getString(Constants.PICTURE_URI, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.PICTURE_URI, value)
            editor.apply()
        }
    //save patient profile picture id(use this to update profile)
    var profilePictureID : String?
        get() = sharedPref.getString(Constants.PROFILE_PICTURE_ID, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.PROFILE_PICTURE_ID, value)
            editor.apply()
        }

    //save patient email(use this prepopulate login screen)
    var profileEmail : String?
        get() = sharedPref.getString(Constants.PROFILE_EMAIL, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.PROFILE_EMAIL, value)
            editor.apply()
        }


    //save appointmentId to successfully navigate back to appointment details
    var appointmentId : String?
        get() = sharedPref.getString(Constants.APPOINTMENT_ID, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.APPOINTMENT_ID, value)
            editor.apply()
        }

    //save locale to manage languege in app
    var selectedLocale : String?
        get() = sharedPref.getString(Constants.LOCALE, "")
        set(value){
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(Constants.LOCALE, value)
            editor.apply()
        }

    //save user Profile
    fun savePatientsProfileResponse(userInfo: GetPatientsProfileResponse?, context: Context) {
        if (userInfo != null){
            saveJsonData(Constants.PATIENTS_PROFILE, Gson().toJson(userInfo), context)
        }
    }

    fun clearPatientsProfileResponse(context: Context){
        saveJsonData(Constants.PATIENTS_PROFILE, "", context)
    }

    fun getPatientsProfileResponse(context: Context): GetPatientsProfileResponse? {
        val jsonData = readJsonData(Constants.PATIENTS_PROFILE, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<GetPatientsProfileResponse>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save list of Patients Appointments
    fun savePatientAppointments(appointments: List<Appointment>?, context: Context) {
        if(appointments != null){
            saveJsonData(Constants.PATIENTS_APPOINTMENT, Gson().toJson(appointments), context)
        }
    }

    fun clearAppointments( context: Context){
        saveJsonData(Constants.PATIENTS_APPOINTMENT, "", context)
    }

    fun getPatientsAppointments(context: Context): List<Appointment>? {
        val jsonData = readJsonData(Constants.PATIENTS_APPOINTMENT, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<List<Appointment>>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save list of Appointment Attachment
    fun saveAppointmentAttachments(attachments: List<Attachment>?, context: Context) {
        if(attachments != null){
            saveJsonData(Constants.APPOINTMENT_ATTACHMENTS, Gson().toJson(attachments), context)
        }
    }

    fun clearAppointmentAttachments( context: Context){
        saveJsonData(Constants.APPOINTMENT_ATTACHMENTS, "", context)
    }

    fun getAppointmentAttachments(context: Context): List<Attachment>? {
        val jsonData = readJsonData(Constants.APPOINTMENT_ATTACHMENTS, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<List<Attachment>>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save IDs of uploaded appointment images
    fun saveAppointmentImages(images: List<MUploadedFile>?, context: Context) {
        if (images != null){
            saveJsonData(Constants.APPOINTMENT_IMAGES, Gson().toJson(images), context)
        }
    }

    fun clearAppointmentImages(context: Context){
        saveJsonData(Constants.APPOINTMENT_IMAGES, "", context)
    }

    fun getAppointmentImages(context: Context): List<MUploadedFile>? {
        val jsonData = readJsonData(Constants.APPOINTMENT_IMAGES, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<List<MUploadedFile>>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save the chosen FreeTime(TimeSlot)
    fun saveTimeSlot(timeSlot: FreeTime?, context: Context) {
        if (timeSlot == null) {
            saveJsonData(Constants.TIME_SLOT, "", context)
        } else {
            saveJsonData(Constants.TIME_SLOT, Gson().toJson(timeSlot), context)
        }
    }

    fun getTimeSlot(context: Context): FreeTime? {
        val jsonData = readJsonData(Constants.TIME_SLOT, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<FreeTime>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save the date of appointment
    fun saveNDate(day: Date, context: Context) {
        saveJsonData(Constants.NDATE, Gson().toJson(day), context)
    }

    fun clearNDate(context: Context){
        saveJsonData(Constants.NDATE, "", context)
    }

    fun getNDate(context: Context): Date? {
        val jsonData = readJsonData(Constants.NDATE, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<Date>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    //save supportReasons get at Login
    fun saveSupportReason(supportReasons: List<Reason>?, context: Context) {
        if (supportReasons == null) {
            saveJsonData(Constants.SUPPORT_REASONS, "", context)
        } else {
            saveJsonData(Constants.SUPPORT_REASONS, Gson().toJson(supportReasons), context)
        }
    }

    fun getSupportReason(context: Context): List<Reason>? {
        val jsonData = readJsonData(Constants.SUPPORT_REASONS, context)
        return if(jsonData == null){
            null
        } else {
            val turnsType = object : TypeToken<List<Reason>>() {}.type
            Gson().fromJson(jsonData, turnsType!!)
        }
    }


    fun clearData(context: Context){
        clearAppointmentImages(context)
        clearAppointments(context)
        clearPatientsProfileResponse(context)
        accessToken = null
        refreshToken = null
        profilePictureID = null
    }
}