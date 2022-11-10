package com.dentify.dentify.ui.fragments.appointmentDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.apiModel.request.PutCancelAppointmentRequest
import com.dentify.dentify.databinding.FragmentAppointmentDetailsBinding
import com.dentify.dentify.databinding.PopupCancelAppointmentBinding
import com.dentify.dentify.enum.AppointmentStatus
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.*
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AppointmentDetailsFragment : BaseFragment() {
    lateinit var binding: FragmentAppointmentDetailsBinding
    val viewModel: DetailsViewModel by viewModels()
    lateinit var appointmentId: String
    var status: Int = 0
    lateinit var dfSummary: SimpleDateFormat
    var isFromHomeFragment = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentDetailsBinding.inflate(layoutInflater)
        initializeDateFormater()
        getAppointmentId()
        doOnBackPressed()
        topBackButtom()
        return binding.root
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
            if(arguments?.getString(Constants.APPOINTMENT_ID) != null){
                appointmentId = arguments?.getString(Constants.APPOINTMENT_ID)!!
            }else{
                appointmentId = StorageWrapper.appointmentId!!
            }
            if (arguments?.getInt(Constants.APPOINTMENT_STATUS) != null){
                status = arguments?.getInt(Constants.APPOINTMENT_STATUS)!!
            }
            if (arguments?.getBoolean(Constants.FROM_HOME, false) != null){
                isFromHomeFragment = arguments?.getBoolean(Constants.FROM_HOME, false)!!
            }
            getAppointent()
        }else if(StorageWrapper.appointmentId != null){
            appointmentId = StorageWrapper.appointmentId!!
            getAppointent()
        }
        binding.btnJoinCall.setOnClickListener {
            checkRoomCreated()
        }
        binding.btnCancelAppointment.setOnClickListener {
            cancelReasonPopup()
        }
        binding.btnViewAttachments.setOnClickListener {
            getAppointentAttachments()
        }
    }

    private fun checkRoomCreated(){
        viewModel.getRoomCreatedResponse(appointmentId, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                val bundle = Bundle()
                bundle.putString(Constants.ROOM_ID, appointmentId)
                findNavController().navigate(R.id.videoCallFragment, bundle)
            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), getString(R.string.room_is_not_created), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setView(model: Appointment){
        binding.tvInitials.text = Util.initials(model.clinicianName)
        if (!model.clinicianName.isNullOrEmpty()){
            binding.tvDoctorName.text = model.clinicianName
        }
        if (model.status != null){
            status = model.status!!.toInt()
            checkStatus(model)
        }
        var startTimeString = ""
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

    private fun checkStatus(model: Appointment){
        val timeZoneParser = Util.dfParser
        timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
        val nowTime = Calendar.getInstance().time
        if (status == AppointmentStatus.Active.value && timeZoneParser.parse(model.dateTo).before(nowTime)){
            binding.btnJoinCall.isVisible = false
            binding.btnCancelAppointment.isVisible = false
            binding.clAppointmnetDetails.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_past_appointment_item)
            binding.tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(binding.tvTitle, getString(R.string.has_not_been_marked_as_done))
        }else if (status == AppointmentStatus.Done.value){
            binding.btnJoinCall.isVisible = false
            binding.btnCancelAppointment.isVisible = false
            binding.clAppointmnetDetails.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_done_appointment_item)
            binding.tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(binding.tvTitle, getString(R.string.has_been_marked_as_done))
        }else if(status == AppointmentStatus.Canceled.value){
            binding.btnJoinCall.isVisible = false
            binding.btnCancelAppointment.isVisible = false
            binding.clAppointmnetDetails.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_canceled_appointment_item)
            binding.tvTitle.text = requireContext().resources.getString(R.string.canceled_appointment)
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

    private fun cancelReasonPopup(){
        val popupView = PopupCancelAppointmentBinding.inflate(layoutInflater)

        popupView.btnCancelPopup.setOnClickListener {
            if (popupView.etCancelReason.text.isNullOrEmpty()){
                popupView.etCancelReason.error = getString(R.string.required_field)
                popupView.etCancelReason.requestFocus()
            }else{
                cancelAppointment(popupView.etCancelReason.text.toString())
            }
        }
        popupView.btnDiscardPopup.setOnClickListener {
            dismissPopup()
        }
        showPopup(popupView.root, binding.root)
    }

    private fun topBackButtom(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun doOnBackPressed(){
        if (isFromHomeFragment == true){
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.appointmentDetailsFragment, true)
                    .build()
                findNavController().navigate(R.id.homeFragment,null, navOptions )
            }
        }else{
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .build()
                findNavController().navigate(R.id.appointmentsFragment,null, navOptions )
            }
        }
    }

    private fun cancelAppointment(reason: String){
        val body = PutCancelAppointmentRequest()
        body.reason = reason

        viewModel.cancelAppointment(appointmentId, body, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                viewModel.getAppointments(requireContext(), object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        dismissPopup()
                        binding.progressBar.isVisible = false
                        findNavController().popBackStack(R.id.homeFragment, false)
                    }
                    override fun onFailure(message: String?) {
                        dismissPopup()
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                dismissPopup()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}