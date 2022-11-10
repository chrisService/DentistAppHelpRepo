package com.dentify.dentify.ui.fragments.home

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.SuccessListener
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.FragmentHomeBinding
import com.dentify.dentify.enum.AppointmentStatus
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.reciever.DentifyRequestReceiver
import com.dentify.dentify.ui.MainActivity
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: AppointmentVPAdapter
    lateinit var requestList: List<Appointment>
    private val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        showLanguageSpinner(false)
        registerRequestReciever()
        initializeReviewFlow()
        setTitleAndPhoto()
        setAdapter()
        openSchedule()
        doOnBackPressed()
        openRequestDetails()
        StorageWrapper.selectedAppointmentReason = Constants.CLEAR_APPOINTMENT_REASON

        return binding.root
    }

    private fun setTitleAndPhoto() {
        var welcomingMessageString = ""
        val compareAfternoonCalendar = Calendar.getInstance()
        compareAfternoonCalendar.set(Calendar.HOUR_OF_DAY, 12)
        val compareEveningStartCalendar = Calendar.getInstance()
        compareEveningStartCalendar.set(Calendar.HOUR_OF_DAY, 19)
        val compareEveningEndCalendar = Calendar.getInstance()
        compareEveningEndCalendar.set(Calendar.HOUR_OF_DAY, 6)
        val nowCalendar = Calendar.getInstance()
        binding.tvTitle.text = "${StorageWrapper.getPatientsProfileResponse(requireContext())?.patient?.fullName?.split(' ')?.get(0)},"
        if(nowCalendar.time.before(compareEveningEndCalendar.time)){
            welcomingMessageString = getString(R.string.good_evening)
        }else if(nowCalendar.time.before(compareAfternoonCalendar.time)){
            welcomingMessageString = getString(R.string.good_morning)
        }else if(nowCalendar.time.after(compareEveningStartCalendar.time)){
            welcomingMessageString = getString(R.string.good_evening)
        }else {
            welcomingMessageString = getString(R.string.good_afternoon)
        }
        binding.tvTitleGreeting.text = welcomingMessageString

        val profileImage = StorageWrapper.profilePictureUri
        if (!profileImage.isNullOrEmpty()){
            binding.ivProfilePic.loadCircularImage(profileImage, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            binding.rlProfilePicture.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }else{
            binding.tvInitials.text = Util.initials(StorageWrapper.getPatientsProfileResponse(requireContext())?.patient?.fullName)
        }
    }

    private fun registerRequestReciever(){
        val intentFilter = IntentFilter("request.action.intent")
        val broadcastReceiver = DentifyRequestReceiver(object: SuccessListener {
            override fun onSuccess(message: String?) {
                viewModel.getAppointments(requireContext(), object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        updateAppointmentsList()
                    }
                    override fun onFailure(message: String?) {
                    }
                })
            }
        })
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)
    }

    fun initializeReviewFlow(){
        (activity as MainActivity).appReview()
    }

    private fun setAdapter() {

        adapter = AppointmentVPAdapter(requireContext(), mutableListOf(), object :
            ViewModelClickListener<Appointment> {
            override fun onClick(item: Appointment) {
                val bundle = Bundle()
                bundle.putString(Constants.APPOINTMENT_ID, item.id)
                bundle.putBoolean(Constants.FROM_HOME, true)
                findNavController().navigate(R.id.appointmentDetailsFragment, bundle)
            }
        })
        binding.vpAppopintments.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.vpAppopintments,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                }
            }).attach()

        updateAppointmentsList()
    }

    private fun updateAppointmentsList(){
        val allAppointmentsList = StorageWrapper.getPatientsAppointments(requireContext())
        var appointmentList = mutableListOf<Appointment>()
        var sortedAppointmentList = mutableListOf<Appointment>()
        val nowTime = Calendar.getInstance().time
        val timeZoneParser = Util.dfParser
        timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
        if (!allAppointmentsList.isNullOrEmpty()) {
            appointmentList = allAppointmentsList.filter {it.status == AppointmentStatus.Active.valueString && timeZoneParser.parse(it.dateTo).after(nowTime) } as MutableList<Appointment>
            sortedAppointmentList = appointmentList.sortedBy { it.dateFrom }.toMutableList()
            requestList = allAppointmentsList.filter { it.status == AppointmentStatus.Requested.valueString && timeZoneParser.parse(it.dateTo).after(nowTime) }
            if (!requestList.isEmpty()){
                binding.clGreetings.visibility = View.GONE
                binding.clRequests.visibility = View.VISIBLE
                binding.clRequests.post { //TODO Check this
                    if (requestList.size == 1){
                        binding.tvRequestsText.text = "1 new appointment request"
                    }else if (requestList.size > 1){
                        binding.tvRequestsText.text = "${requestList.size.toString()} new appointment requests"
                    }
                }
            }
        }
        adapter.updateData(sortedAppointmentList)
    }

    private fun openSchedule() {
        binding.clSchedule.setOnClickListener {
            StorageWrapper.clearNDate(requireContext())
            findNavController().navigate(R.id.pickDateAndTimeFragment)
        }
    }

    private fun openRequestDetails(){
        binding.clRequests.setOnClickListener {
            if (requestList.size == 1){
                val bundle = Bundle()
                bundle.putString(Constants.REQUEST_ID, requestList.firstOrNull()?.id)
                bundle.putBoolean(Constants.FROM_HOME, true)
                findNavController().navigate(R.id.requestDetailsFragment, bundle)
            }else{
                findNavController().navigate(R.id.requestsFragment)
            }
        }
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }
}