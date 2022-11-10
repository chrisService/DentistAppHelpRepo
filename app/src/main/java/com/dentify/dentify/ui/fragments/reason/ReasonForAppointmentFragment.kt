package com.dentify.dentify.ui.fragments.reason

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.MAppointmentReason
import com.dentify.dentify.databinding.FragmentReasonForAppointmentBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper


class ReasonForAppointmentFragment : BaseFragment() {
   lateinit var binding: FragmentReasonForAppointmentBinding
   lateinit var adapter: ReasonsRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReasonForAppointmentBinding.inflate(layoutInflater)
        showLanguageSpinner(false)
        topBackButtom()
        setAdapters()

        return binding.root
    }

    var handler: Handler? = null

    private fun setAdapters(){
        adapter = ReasonsRVAdapter(requireContext(), Constants.getReasonsList(requireContext()), object:
            ViewModelClickListener<MAppointmentReason> {
            override fun onClick(item: MAppointmentReason) {
                if (handler != null){
                    handler!!.removeCallbacksAndMessages(null)
                }
                handler = Handler(Looper.getMainLooper())
                handler!!.postDelayed({
                    StorageWrapper.selectedAppointmentReason = item.orderNumber
                    findNavController().navigate(R.id.openImageUpload) },1000)
            }
        })
        binding.rvReasons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReasons.adapter = adapter
    }

    private fun topBackButtom(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


}