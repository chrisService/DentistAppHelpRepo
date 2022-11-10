package com.dentify.dentify.apiModel.request

class PostPatientsAppointmentRequest {
    var clinicId: String? = null
    var clinicianId: String? = null
    var reason: Int? = null
    var appointmentType: String? = null
    var dateFrom: String? = null
    var dateTo: String? = null
    var attachments: List<String>? = null
}
