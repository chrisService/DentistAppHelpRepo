package com.dentify.dentify.ui.fragments.appoitmentRequests

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
import com.dentify.dentify.apiModel.request.PutCancelAppointmentRequest
import com.dentify.dentify.databinding.FragmentRequestDetailsBinding
import com.dentify.dentify.databinding.PopupDeclineRequestBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.ui.fragments.appointmentDetails.DetailsViewModel
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RequestDetailsFragment : BaseFragment() {

    lateinit var binding: FragmentRequestDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()
    lateinit var appointmentId: String
    lateinit var dfSummary: SimpleDateFormat
    var requestNote: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestDetailsBinding.inflate(layoutInflater)
        initializeDateFormater()
        getAppointmentId()
        buttonClicks()
        backBtn()

        return binding.root
    }

    private fun buttonClicks(){
        binding.btnViewAttachments.setOnClickListener {
            getAppointentAttachments()
        }
        binding.btnViewNotes.setOnClickListener {
            if(!requestNote.isNullOrEmpty()){
                val bundle = Bundle()
                bundle.putString(Constants.APPOINTMENT_REQUEST_NOTE, requestNote)
                findNavController().navigate(R.id.requestNotesFragment, bundle)
            }else{
                Toast.makeText(requireContext(), getString(R.string.not_any_request_notes), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnAccept.setOnClickListener {
            acceptRequest()
        }
        binding.btnDecline.setOnClickListener {
            declinePopup()
        }
    }

    private fun initializeDateFormater(){
        if(StorageWrapper.selectedLocale.isNullOrEmpty()){
            dfSummary = SimpleDateFormat("EEEE dd.MM, yyyy")
        }else{
            dfSummary = SimpleDateFormat("EEEE dd.MM, yyyy", Locale(StorageWrapper.selectedLocale!!))
        }
    }

    private fun getAppointmentId(){
        if (arguments != null){
            if(arguments?.getString(Constants.REQUEST_ID) != null){
                appointmentId = arguments?.getString(Constants.REQUEST_ID)!!
            }
            getAppointent()
        }
    }

    private fun getAppointent(){
        viewModel.getAppointment(appointmentId, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                setView(viewModel.appointmentResponse)
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setView(model: Appointment){
        binding.tvInitials.text = Util.initials(model.clinicianName)
        if (!model.clinicianName.isNullOrEmpty()){
            binding.tvDoctorName.text = model.clinicianName
        }
        var startTimeString = ""
        if (!model.requestNote.isNullOrEmpty()){
            requestNote = model.requestNote
        }
        if (!model.dateFrom.isNullOrEmpty()){
            val nDateFrom = Util.dfParser.parse(model.dateFrom!!)
            binding.tvDate.text = dfSummary.format(nDateFrom!!)
            startTimeString = Util.dfClock.format(nDateFrom)
        }
        var endTimeString = ""
        if (!model.dateTo.isNullOrEmpty()){
            val nDateTo = Util.dfParser.parse(model.dateTo!!)
            endTimeString = Util.dfClock.format(nDateTo!!)
        }
        binding.tvTime.text = "$startTimeString - $endTimeString"
        binding.tvClinic.text = model.clinicName
        if(model.reason != null){
            binding.tvReasons.text = Constants.getReasonsList(requireContext()).first { it.orderNumber ==  model.reason!!.toInt()}.title
        }
        if (!model.clinicianProfileImageUri.isNullOrEmpty()){
            binding.ivClinitianImage.loadCircularImage(model.clinicianProfileImageUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }

    private fun getAppointentAttachments(){
        viewModel.getAppointmentAttachments(requireContext(), appointmentId, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                if (viewModel.appointmentAttachmentsResponse.attachments.isNullOrEmpty()){
                    Toast.makeText(requireContext(), getString(R.string.no_files_attached), Toast.LENGTH_SHORT).show()
                }else{
                    findNavController().navigate(R.id.attachmentsFragment)
                }
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun acceptRequest(){
        viewModel.putAcceptRequest(appointmentId, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                viewModel.getAppointments(requireContext(), object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.requestDetailsFragment, true)
                            .build()
                        findNavController().navigate(R.id.homeFragment,null, navOptions )
                    }
                    override fun onFailure(message: String?) {
                    }
                })
            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()


            }
        })
    }

    private fun declineRequest(body: PutCancelAppointmentRequest){
        viewModel.putDeclineRequest(appointmentId, body, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                viewModel.getAppointments(requireContext(), object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        dismissPopup()
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.requestDetailsFragment, true)
                            .build()
                        findNavController().navigate(R.id.homeFragment,null, navOptions )
                    }
                    override fun onFailure(message: String?) {
                    }
                })

            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun declinePopup(){
        val popupView = PopupDeclineRequestBinding.inflate(layoutInflater)

        popupView.btnDiscardPopup.setOnClickListener {
            dismissPopup()
        }

        popupView.btnCancelPopup.setOnClickListener {
            if (!popupView.etCancelReason.text.toString().isEmpty()){
                val body = PutCancelAppointmentRequest()
                body.reason = popupView.etCancelReason.text.toString()
                declineRequest(body)
            }else{
                Toast.makeText(requireContext(), getString(R.string.write_your_reason_for_declining), Toast.LENGTH_SHORT).show()
            }
        }


        showPopup(popupView.root, binding.root)
    }


    private fun backBtn(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}