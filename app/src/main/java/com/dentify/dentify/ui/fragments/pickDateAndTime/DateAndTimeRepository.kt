package com.dentify.dentify.ui.fragments.pickDateAndTime

import com.dentify.dentify.api.ApiService
import com.dentify.dentify.apiModel.query.GetClinitianFreeTimesQuery
import com.dentify.dentify.apiModel.response.GetClinitianFreeTimesResponse
import com.dentify.dentify.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class DateAndTimeRepository@Inject constructor(
    private val apiService: ApiService
) : MainRepository(apiService) {

    suspend fun getClinitianFreeTimes(query: GetClinitianFreeTimesQuery): Response<GetClinitianFreeTimesResponse> {

        val queryData =  hashMapOf<String, Any>()
        queryData["clinicianId"] = query.clinicianId
        queryData["clinicId"] = query.clinicId
        queryData["date"] = query.date

        return  apiService.getFromApi("api/clinicians/appointments/free-times", queryData)
    }
}