package com.dentify.dentify.ui.fragments.signUp

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.query.GetBankIdCollectQuery
import com.dentify.dentify.apiModel.request.PostPatientsPatientsRequest
import com.dentify.dentify.apiModel.response.GetBankIdCollectResponse
import com.dentify.dentify.apiModel.response.GetPatientsInvitationEmailResponse
import com.dentify.dentify.enum.WelcomeCondition
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.ErrorHandler
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: SignUpRepository) :
    MainViewModel(repository) {

    fun postPatient(requestBody: PostPatientsPatientsRequest, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.postPatients(requestBody)
            if (response.code() == 200) {
                StorageWrapper.welcomeCondition = WelcomeCondition.Welcome.value
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

    lateinit var emailResponse: GetPatientsInvitationEmailResponse

    fun getEmail(invitationId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getInvitationEmail(invitationId)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                emailResponse = Gson().fromJson(jSonObject.toString(), GetPatientsInvitationEmailResponse::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }


    lateinit var bankIdCollectResponse: GetBankIdCollectResponse

    fun getBankIdCollect(query: GetBankIdCollectQuery, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getBankIdCollect(query)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                bankIdCollectResponse =
                    Gson().fromJson(jSonObject.toString(), GetBankIdCollectResponse::class.java)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }
}