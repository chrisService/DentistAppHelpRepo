package com.dentify.dentify.ui.fragments.confirmation

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
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.apiModel.response.GetPatientsProfileResponse
import com.dentify.dentify.databinding.FragmentConfirmationBinding
import com.dentify.dentify.ui.MainActivity
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ConfirmationFragment : BaseFragment() {

    lateinit var binding: FragmentConfirmationBinding
    private val viewModel: ConformationFragmentViewModel by viewModels()
    private lateinit var newAppointmentId: String
    private lateinit var dfConformationDate: SimpleDateFormat
    private lateinit var profile: GetPatientsProfileResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmationBinding.inflate(layoutInflater)
        showLanguageSpinner(false)
        initializeDateFormater()
        setView()
        goToAppointmentsClick()
        startReviewFlow()

        return binding.root
    }

    fun startReviewFlow(){
        (activity as MainActivity).startReviewFlow()
    }

    private fun initializeDateFormater(){
        if(StorageWrapper.selectedLocale.isNullOrEmpty()){
            dfConformationDate = SimpleDateFormat("MMMM dd, yyyy")
        }else{
            dfConformationDate = SimpleDateFormat("MMMM dd, yyyy", Locale(StorageWrapper.selectedLocale!!))
        }
    }

    private fun setView(){

        if (StorageWrapper.getNDate(requireContext()) != null){
            binding.tvAppointmentDate.text = dfConformationDate.format(StorageWrapper.getNDate(requireContext())!!)
        }
        if (!StorageWrapper.getTimeSlot(requireContext())?.dateFrom.isNullOrEmpty()){
            val dateFrom = Util.dfParser.parse(StorageWrapper.getTimeSlot(requireContext())?.dateFrom!!)
            binding.tvAppointmnetTime.text = Util.dfClock.format(dateFrom!!)
        }
        if (StorageWrapper.getPatientsProfileResponse(requireContext()) != null){
            profile = StorageWrapper.getPatientsProfileResponse(requireContext())!!
            binding.tvAppointmentClinic.text = profile.clinic?.clinicName
            if (profile.clinic?.location != null){
                val address = profile.clinic?.location!!.address
                val city = profile.clinic?.location!!.city
                binding.tvAppointmentAddress.text = "$address, $city"

            }
            if (profile.clinic?.logoUri != null){
                binding.ivClinicImage.loadCircularImage(profile.clinic?.logoUri, 12F, ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (arguments != null){
                newAppointmentId = arguments?.getString(Constants.APPOINTMENT_ID)!!
            }
        }
        getAppointent()
    }

    private fun goToAppointmentsClick(){
        binding.btnGoToAppointments.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.conformationFragment, true)
                .build()
            findNavController().navigate(R.id.appointmentsFragment,null, navOptions )
        }
    }

    private fun functionForVisualPurpose(model: Appointment){
        binding.llConfirmedAppointment.addView(viewModel.appointmentView(requireContext(), model, binding.llConfirmedAppointment, this))
    }

    private fun getAppointent(){
        viewModel.getAppointment(requireContext(), newAppointmentId, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                functionForVisualPurpose(viewModel.appointmentResponse)
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}