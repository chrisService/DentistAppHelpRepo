package com.dentify.dentify.ui.fragments.pickDateAndTime

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.query.GetClinitianFreeTimesQuery
import com.dentify.dentify.apiModel.response.GetClinitianFreeTimesResponse
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DateAndTimeViewModel @Inject constructor(
    private val repository: DateAndTimeRepository
) : MainViewModel(repository) {

    lateinit var freeTimesResponse: GetClinitianFreeTimesResponse

    fun getFreeTimes(query: GetClinitianFreeTimesQuery, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getClinitianFreeTimes(query)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                freeTimesResponse = Gson().fromJson(
                    jSonObject.toString(),
                    GetClinitianFreeTimesResponse::class.java
                )
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}