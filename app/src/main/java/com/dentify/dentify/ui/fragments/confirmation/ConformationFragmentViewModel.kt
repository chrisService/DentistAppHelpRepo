package com.dentify.dentify.ui.fragments.confirmation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.VhAppointmentItemBinding
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.repository.MainRepository
import com.dentify.dentify.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConformationFragmentViewModel @Inject constructor(
    private val repository: MainRepository
) : MainViewModel(repository) {

    fun appointmentView(context: Context, model: Appointment, container: ViewGroup, fragment: ConfirmationFragment): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.vh_appointment_item, container, false)
        val binding = VhAppointmentItemBinding.bind(view)
        val dfAppointment: SimpleDateFormat
        if (StorageWrapper.selectedLocale.isNullOrEmpty()){
            dfAppointment = SimpleDateFormat("EEE, dd.MM")
        }else{
            dfAppointment = SimpleDateFormat("EEE, dd.MM", Locale(StorageWrapper.selectedLocale!!))
        }
        var hourFrom = ""
        var hourTo = ""
        if (!model.dateFrom.isNullOrEmpty()){
            val dateFrom = Util.dfParser.parse(model.dateFrom!!)
            binding.tvDate.text = dfAppointment.format(dateFrom!!)
            hourFrom = Util.dfClock.format(dateFrom)
        }
        if (!model.dateTo.isNullOrEmpty()){
            val dateTo = Util.dfParser.parse(model.dateTo!!)
            hourTo = Util.dfClock.format(dateTo!!)
        }
        binding.tvDoctorName.text = model.clinicianName
        binding.tvClinic.text = model.clinicName
        binding.tvHour.text = "$hourFrom - $hourTo"
        binding.tvInitials.text = Util.initials(model.clinicianName)
        if (StorageWrapper.getPatientsProfileResponse(context)!!.clinician?.profileImageUri != null) {
            binding.ivClinitianImage.loadCircularImage(StorageWrapper.getPatientsProfileResponse(context)!!.clinician?.profileImageUri, 7F, ContextCompat.getColor(context, R.color.white))
            binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)

        }
        binding.root.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            val bundle = Bundle()
            bundle.putString(Constants.APPOINTMENT_ID, model.id)
            fragment.findNavController().navigate(R.id.appointmentDetailsFragment, bundle, navOptions )
        }

        return view
    }

    lateinit var appointmentResponse: Appointment
    private var updatedAppointments = mutableListOf<Appointment>()

    fun getAppointment(context: Context, appointmentId: String, apiListener: ViewModelApiListener) {
        apiListener.onStarted()
        viewModelScope.launch {
            val response = repository.getAppointment(appointmentId)
            if (response.code() == 200) {
                val jSonObject = JSONObject(Gson().toJson(response.body()))
                appointmentResponse = Gson().fromJson(jSonObject.toString(), Appointment::class.java)
                if(!StorageWrapper.getPatientsAppointments(context).isNullOrEmpty()){
                    updatedAppointments = StorageWrapper.getPatientsAppointments(context) as MutableList<Appointment>
                }
                updatedAppointments.add(appointmentResponse)
                StorageWrapper.savePatientAppointments(updatedAppointments, context)
                apiListener.onSuccess()
            } else {
                ErrorHandler.init(response, apiListener)
            }
        }
    }

}