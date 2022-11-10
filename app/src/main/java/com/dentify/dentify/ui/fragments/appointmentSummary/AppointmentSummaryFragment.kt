package com.dentify.dentify.ui.fragments.appointmentSummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostPatientsAppointmentRequest
import com.dentify.dentify.apiModel.response.GetPatientsProfileResponse
import com.dentify.dentify.databinding.FragmentAppointmentSummaryBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AppointmentSummaryFragment : BaseFragment() {
    lateinit var binding: FragmentAppointmentSummaryBinding
    private val viewModel: SummaryViewModel by viewModels()
    lateinit var profile: GetPatientsProfileResponse
    private lateinit var dfSummary: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentSummaryBinding.inflate(layoutInflater)
        showLanguageSpinner(false)
        initializeDateFormater()
        setView()
        topBackButton()
        confirmClick()

        return binding.root
    }

    private fun initializeDateFormater(){
        if(StorageWrapper.selectedLocale.isNullOrEmpty()){
            dfSummary = SimpleDateFormat("EEEE dd.MM, yyyy")
        }else{
            dfSummary = SimpleDateFormat("EEEE dd.MM, yyyy", Locale(StorageWrapper.selectedLocale!!))
        }
    }

    private fun setView(){
        profile = StorageWrapper.getPatientsProfileResponse(requireContext())!!
        if (!profile.clinician?.fullName.isNullOrEmpty()){
            binding.tvInitials.text = Util.initials(profile.clinician?.fullName)
            binding.tvDoctorName.text = profile.clinician?.fullName
        }
        if (StorageWrapper.getNDate(requireContext()) != null){
            binding.tvDate.text = dfSummary.format(StorageWrapper.getNDate(requireContext())!!)
        }
        var startTimeString = ""
        var endTimeString = ""
        val timeSlot = StorageWrapper.getTimeSlot(requireContext())
        if (timeSlot != null){
            val nDateFrom = Util.dfParser.parse(timeSlot.dateFrom!!)
            binding.tvDate.text = dfSummary.format(nDateFrom!!)
            startTimeString = Util.dfClock.format(nDateFrom)
            val nDateTo = Util.dfParser.parse(timeSlot.dateTo!!)
            endTimeString = Util.dfClock.format(nDateTo!!)
        }
        binding.tvTime.text = "$startTimeString - $endTimeString"
        binding.tvClinic.text = profile.clinic?.clinicName
        binding.tvReasons.text = Constants.getReasonsList(requireContext()).first { it.orderNumber ==  StorageWrapper.selectedAppointmentReason}.title
        if (!profile.clinician?.profileImageUri.isNullOrEmpty()){
            binding.ivClinitianImage.loadCircularImage(profile.clinician?.profileImageUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }

    private fun confirmClick(){
        binding.btnConfirm.setOnClickListener {
            postAppointment()
        }
    }

    private fun postAppointment(){
        val body = PostPatientsAppointmentRequest()
        body.clinicId = profile.clinic?.id
        body.clinicianId = profile.clinician?.id
        body.reason = StorageWrapper.selectedAppointmentReason
        body.dateFrom = StorageWrapper.getTimeSlot(requireContext())?.dateFrom
        body.dateTo = StorageWrapper.getTimeSlot(requireContext())?.dateTo
        body.appointmentType = Constants.VIDEO_CALL_APPOINTMENT_TYPE
        val appointmentImages = mutableListOf<String>()
        val uploadedImages = StorageWrapper.getAppointmentImages(requireContext())
        if (!uploadedImages.isNullOrEmpty()){
            for(item in uploadedImages){
                appointmentImages.add(item.imageId!!)
            }
        }
        body.attachments = appointmentImages

        viewModel.postPatientAppointment(body, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .build()
                val bundle = Bundle()
                bundle.putString(Constants.APPOINTMENT_ID, viewModel.newAppointmentResponse.id)
                findNavController().navigate(R.id.conformationFragment, bundle, navOptions )
                StorageWrapper.clearAppointmentImages(requireContext())
                StorageWrapper.selectedAppointmentReason = Constants.CLEAR_APPOINTMENT_REASON
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                StorageWrapper.clearAppointmentImages(requireContext())
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun topBackButton(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}