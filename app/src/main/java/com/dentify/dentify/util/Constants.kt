package com.dentify.dentify.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.dentify.dentify.R
import com.dentify.dentify.apiModel.model.Language
import com.dentify.dentify.apiModel.model.MAppointmentReason
import com.dentify.dentify.apiModel.model.MSupportReason
import com.twilio.video.IsacCodec
import com.twilio.video.Vp8Codec

object Constants {

    const val BEARER = "Bearer "
    const val SHARED_PREFS_NAME = "Dentify"
    const val SAVED_JSON_DATA_NAME = "DentifyData"
    const val ACCESS_TOKEN = "AccessToken"
    const val FCM_TOKEN = "fcmToken"
    const val REFRESH_TOKEN = "RefreshToken"
    const val PATIENTS_PROFILE = "PatientsProfile"
    const val PATIENTS_APPOINTMENT = "PatientsAppointment"
    const val APPOINTMENT_ATTACHMENTS = "AppointmentAttachments"
    const val APPOINTMENT_IMAGES = "AppointmentImages"
    const val SUPPORT_REASONS = "SupportReasons"
    const val TIME_SLOT = "TimeSlot"
    const val NDATE = "nDate"
    const val ORDER_REF = "orederRef"
    const val INVITATION_ID = "invitationId"
    const val APPOINTMENT_ID = "appointmentid"
    const val REQUEST_ID = "requestid"
    const val APPOINTMENT_REQUEST_NOTE = "appointmentRequestNote"
    const val FROM_HOME = "fromHomeFragment"
    const val LOCALE = "locale"
    const val ENGLISH = "en"
    const val SWEDISH = "sv"
    const val ENGLISH_LOCALE = "en-US"
    const val SWEDISH_LOCALE = "sv-SE"
    const val PICTURE_URI = "pictureUri"
    const val PROFILE_PICTURE_ID = "profilePictureID"
    const val PROFILE_EMAIL = "profileEmail"
    const val RESSET_PASSWORD_EMAIL = "ResetPasswordEmail"
    const val ID = "id"
    const val OPEN = "open"
    const val ROOM_ID = "roomid"
    const val PASSWORD = "password"
    const val GENERIC_ERROR = "Something went wrong, please try again."
    const val GMT = "GMT"
    const val VIDEO_CALL_APPOINTMENT_TYPE = "VideoCall"
    const val APPOINTMENT_REASON_ORDER_NUMBER = "orderNumber"
    const val APPOINTMENT_STATUS = "status"
    const val NOISE_SUPRESSION_ON = "noise_suppression=on"
    const val BANK_ID = "bankid"
    const val SET_NEW_PASSWORD_QUERY = "c"
    const val SELECTED_REASON = "selectedReason"
    const val WELCOME_CONDITION = "welcomeCondition"
    const val SPLASH_TEXT = "splashText"
    const val JPG = "jpg"
    const val JPEG = "jpeg"
    const val PNG = "png"
    const val CLEAR_APPOINTMENT_REASON = 0
    const val ADJUST_APPOINTMENT_REASONS_LIST = -1
    const val SPLASH_TEXT_1 = 1
    const val SPLASH_TEXT_2 = 2
    const val SPLASH_SCREEN_FADE_TIME = 2000L
    const val ANDROID_RESOURCES = "android.resource://"
    const val INVALID_CREDENCIALS = "Invalid credentials."
    const val PAST = 7
    const val CANCELED = 8
    const val REVIEW_CONDITION = "reviewCondition"
    const val DEACTIVATE = "DEACTIVATE"

    const val ROOM_NOT_CREATED = "0"
    const val ROOM_CREATED = "1"
    const val APPOINTMENT_ACTIVE = "1"
    const val APPOINTMENT_DONE = "2"
    const val APPOINTMENT_CANCELED = "3"
    const val APPOINTMENT_REQUESTED = "4"
    const val APPOINTMENT_REQUEST_DENIED = "5"

    //notification type
    const val NOTIFICATION_TYPE_ROOM_CREATED = "ROOM_CREATED"
    const val NOTIFICATION_TYPE_CLINICIAN_JOINED = "DENTIST_JOINED"
    const val NOTIFICATION_TYPE_APPOINTMENT_REMINDER = "APPOINTMENT_START_REMINDER"
    const val NOTIFICATION_TYPE_APPOINTMENT_REQUEST = "APPOINTMENT_REQUEST_REMINDER"

    //twilio
    const val PREF_AUDIO_CODEC_DEFAULT = IsacCodec.NAME
    const val PREF_VIDEO_CODEC_DEFAULT = Vp8Codec.NAME
    const val PREF_VP8_SIMULCAST_DEFAULT = false

    //appointmentReasonsListOrderIntegers
    const val TOOTHACHE = 1
    const val DAMAGED_TOOTH = 2
    const val GUMS = 3
    const val WISDOM_TEETH = 4
    const val JAW_PROBLEM = 5
    const val FILLING = 6
    const val TOOTH_SENSITIVITY = 7
    const val AESTHETIC_DENTISTRY = 8
    const val OTHER = 9

    fun getReasonsList(context: Context): List<MAppointmentReason>{
        val reasons = ArrayList<MAppointmentReason>()
        reasons.add(MAppointmentReason(context.getString(R.string.toothache), TOOTHACHE , ContextCompat.getDrawable(context, R.drawable.ic_toothache_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.tooth_sensitivity), TOOTH_SENSITIVITY , ContextCompat.getDrawable(context, R.drawable.ic_tooth_sensitivity_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.damaged_tooth), DAMAGED_TOOTH , ContextCompat.getDrawable(context, R.drawable.ic_damaged_tooth_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.gums), GUMS , ContextCompat.getDrawable(context, R.drawable.ic_gums_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.wisdom_teeth), WISDOM_TEETH , ContextCompat.getDrawable(context, R.drawable.ic_wisdom_teeth_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.jaw_problem), JAW_PROBLEM , ContextCompat.getDrawable(context, R.drawable.ic_jaw_problem_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.filling), FILLING , ContextCompat.getDrawable(context, R.drawable.ic_filling_crown_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.aesthetic_dentistry), AESTHETIC_DENTISTRY , ContextCompat.getDrawable(context, R.drawable.ic_aesthetic_dentistry_icon)!!))
        reasons.add(MAppointmentReason(context.getString(R.string.other), OTHER , ContextCompat.getDrawable(context, R.drawable.ic_other_icon)!!))

        return reasons
    }

    const val ENGLISH_LANGUAGE = "English(US)"
    const val SWEDISH_LANGUAGE = "Swedish(SV)"

    fun getSupportedLanguages(): ArrayList<Language>{
        val languages = ArrayList<Language>()
        languages.add(Language(SWEDISH_LANGUAGE, R.drawable.flag_sv))
        languages.add(Language(ENGLISH_LANGUAGE, R.drawable.flag_en))
        return languages
    }

    //supportReasonsListOrderIntegers
    const val TECHNICAL_SUPPORT = 1
    const val SALES_AND_MARKETING = 2
    const val OTHER_SUPPORT = 3

    fun getSupportReasonsList(context: Context): List<MSupportReason>{
        val supportReasons = ArrayList<MSupportReason>()
        supportReasons.add(MSupportReason(context.getString(R.string.technical_support), TECHNICAL_SUPPORT))
        supportReasons.add(MSupportReason(context.getString(R.string.sales_and_marketing), SALES_AND_MARKETING))
        supportReasons.add(MSupportReason(context.getString(R.string.other), OTHER_SUPPORT))

        return supportReasons
    }
}