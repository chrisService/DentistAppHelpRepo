package com.dentify.dentify.apiModel.response


import com.google.gson.annotations.SerializedName
import com.dentify.dentify.apiModel.model.Clinic
import com.dentify.dentify.apiModel.model.Clinician
import com.dentify.dentify.apiModel.model.Patient

data class GetPatientsProfileResponse(
    @SerializedName("patient")
    var patient: Patient?,
    @SerializedName("clinician")
    var clinician: Clinician?,
    @SerializedName("clinic")
    var clinic: Clinic?
)