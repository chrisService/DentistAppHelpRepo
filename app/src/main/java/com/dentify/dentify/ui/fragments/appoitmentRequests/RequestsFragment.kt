package com.dentify.dentify.ui.fragments.appoitmentRequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.FragmentRequestsBinding
import com.dentify.dentify.enum.AppointmentStatus
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.LogoutHandler
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RequestsFragment : Fragment() {

    val viewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentRequestsBinding
    lateinit var adapter: RequestsRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestsBinding.inflate(layoutInflater)

        getAppointments()
        backBtn()

        return binding.root
    }


    private fun setAdapter(){
        val nowTime = Calendar.getInstance().time
        val timeZoneParser = Util.dfParser
        timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)

        adapter = RequestsRVAdapter(requireContext(), StorageWrapper.getPatientsAppointments(requireContext())!!.filter { it.status == AppointmentStatus.Requested.valueString && timeZoneParser.parse(it.dateTo).after(nowTime)} as MutableList<Appointment>, object: ViewModelClickListener<Appointment>{
            override fun onClick(item: Appointment) {
                val bundle = Bundle()
                bundle.putString(Constants.REQUEST_ID, item.id)
                findNavController().navigate(R.id.requestDetailsFragment, bundle)
            }
        })
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequests.adapter = adapter
    }

    private fun backBtn(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
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