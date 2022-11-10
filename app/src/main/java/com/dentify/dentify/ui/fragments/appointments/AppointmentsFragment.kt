package com.dentify.dentify.ui.fragments.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.FragmentAppointmentsBinding
import com.dentify.dentify.enum.AppointmentStatus
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.ui.fragments.home.AppointmentVPAdapter
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.LogoutHandler
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AppointmentsFragment : Fragment() {

    lateinit var binding: FragmentAppointmentsBinding
    private lateinit var upcomingAppointmentsAdapter: AppointmentVPAdapter
    private lateinit var pastAppointmentsAdapter: PastAppointmentRVAdapter
    private lateinit var canceledAppointmentsAdapter: PastAppointmentRVAdapter
    val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentsBinding.inflate(layoutInflater)

        binding.root.post{
            getAppointments()
        }
        doOnBackPressed()

        return binding.root
    }

    private fun setAdapter() {

        upcomingAppointmentsAdapter =
            AppointmentVPAdapter(requireContext(), mutableListOf(), object :
                ViewModelClickListener<Appointment> {
                override fun onClick(item: Appointment) {
                    val bundle = Bundle()
                    bundle.putString(Constants.APPOINTMENT_ID, item.id)
                    bundle.putBoolean(Constants.FROM_HOME, false)
                    findNavController().navigate(R.id.appointmentDetailsFragment, bundle)
                }
            })
        binding.vpAppopintments.adapter = upcomingAppointmentsAdapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.vpAppopintments,
            object : TabLayoutMediator.TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                }
            }).attach()

        pastAppointmentsAdapter =
            PastAppointmentRVAdapter(requireContext(), mutableListOf(), object :
                ViewModelClickListener<Appointment> {
                override fun onClick(item: Appointment) {
                    val bundle = Bundle()
                    bundle.putString(Constants.APPOINTMENT_ID, item.id)
                    bundle.putBoolean(Constants.FROM_HOME, false)
                    findNavController().navigate(R.id.appointmentDetailsFragment, bundle)
                }
            }, Constants.PAST, layoutInflater)
        binding.rvPastAppointments.adapter = pastAppointmentsAdapter

        canceledAppointmentsAdapter =
            PastAppointmentRVAdapter(requireContext(), mutableListOf(), object :
                ViewModelClickListener<Appointment> {
                override fun onClick(item: Appointment) {
                    val bundle = Bundle()
                    bundle.putString(Constants.APPOINTMENT_ID, item.id)
                    bundle.putBoolean(Constants.FROM_HOME, false)
                    findNavController().navigate(R.id.appointmentDetailsFragment, bundle)
                }
            }, Constants.CANCELED, layoutInflater)
        binding.rvCanceledAppointments.adapter = canceledAppointmentsAdapter

        updateAppointmentsList(StorageWrapper.getPatientsAppointments(requireContext()))
    }

    fun updateAppointmentsList(allAppointmentsList: List<Appointment>?){
        var appointmentList = mutableListOf<Appointment>()
        var pastAppointmentList = mutableListOf<Appointment>()
        var canceledAppointmentList = mutableListOf<Appointment>()
        var sortedAppointmentList = mutableListOf<Appointment>()
        var sortedPastAppointmentList = mutableListOf<Appointment>()
        var sortedCanceledAppointmentList = mutableListOf<Appointment>()
        val nowTime = Calendar.getInstance().time
        val timeZoneParser = Util.dfParser
        timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
        if (!allAppointmentsList.isNullOrEmpty()) {
            appointmentList = allAppointmentsList.filter {
                it.status == AppointmentStatus.Active.valueString && timeZoneParser.parse(it.dateTo).after(nowTime)  } as MutableList<Appointment>
            sortedAppointmentList = appointmentList.sortedBy { it.dateFrom }.toMutableList()
        }
        if (!allAppointmentsList.isNullOrEmpty()) {
            pastAppointmentList = allAppointmentsList.filter {
                it.status == AppointmentStatus.Done.valueString || (it.status == AppointmentStatus.Active.valueString &&  timeZoneParser.parse(it.dateTo).before(nowTime) )} as MutableList<Appointment>
            sortedPastAppointmentList = pastAppointmentList.sortedBy { it.dateFrom }.toMutableList()
        }
        if (!allAppointmentsList.isNullOrEmpty()) {
            canceledAppointmentList = allAppointmentsList.filter {
                it.status == AppointmentStatus.Canceled.valueString} as MutableList<Appointment>
            sortedCanceledAppointmentList = canceledAppointmentList.sortedBy { it.dateFrom }.toMutableList()
        }
        upcomingAppointmentsAdapter.updateData(sortedAppointmentList)
        pastAppointmentsAdapter.updateData(sortedPastAppointmentList)
        canceledAppointmentsAdapter.updateData(sortedCanceledAppointmentList)
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.appointmentsFragment, true)
                .build()
            findNavController().navigate(R.id.homeFragment,null, navOptions)
        }
    }

    private fun getAppointments(){
        viewModel.getAppointments(requireContext(), object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                StorageWrapper.clearAppointments(requireContext())
            }
            override fun onSuccess(message: String?) {
                setAdapter()
            }
            override fun onFailure(message: String?) {
                LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
            }
        })
    }

}