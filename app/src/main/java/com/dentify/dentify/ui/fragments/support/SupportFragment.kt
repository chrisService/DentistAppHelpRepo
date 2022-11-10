package com.dentify.dentify.ui.fragments.support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostSupportRequest
import com.dentify.dentify.databinding.FragmentSupportBinding
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment : Fragment() {

    lateinit var binding: FragmentSupportBinding
    var reasonID: String? = null
    val viewModel: SupportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(layoutInflater)

        try {
            setAdapter()
            sendSupportMessage()
        }catch (e: Exception){
          e.printStackTrace()
        }
        doOnBackPressed()

        return binding.root
    }

    private fun setAdapter(){

        val reasonsList = ArrayList<String>()
        for (item in Constants.getSupportReasonsList(requireContext())){
            reasonsList.add(item.title)
        }
        val spinnerAdapterClinics: ArrayAdapter<String> = object : ArrayAdapter<String>(requireContext(), R.layout.vh_spinner_text, reasonsList) {}
        binding.spinnerSubject.adapter = spinnerAdapterClinics
        binding.spinnerSubject.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!StorageWrapper.getSupportReason(requireContext()).isNullOrEmpty()){
                    reasonID = StorageWrapper.getSupportReason(requireContext())?.get(position)?.id!!
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun sendSupportMessage(){
        binding.button.setOnClickListener {
            if (binding.etSubject.text.isNullOrEmpty()){
                binding.etSubject.error = getString(R.string.please_add_subject)
                binding.etSubject.requestFocus()
            }else if(binding.etMessage.text.isNullOrEmpty()){
                binding.etMessage.error = getString(R.string.please_write_your_message)
                binding.etMessage.requestFocus()
            }else{
                val body = PostSupportRequest()
                if(reasonID != null){
                    body.reasonTypeId = reasonID
                }
                body.message = binding.etMessage.text.toString()
                body.subject = binding.etSubject.text.toString()
                viewModel.postSupport(body, object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                        binding.progressBar.isVisible = true
                    }
                    override fun onSuccess(message: String?) {
                        binding.progressBar.isVisible = false
                        binding.etSubject.text!!.clear()
                        binding.etMessage.text!!.clear()
                        Toast.makeText(requireContext(), getString(R.string.thanks_for_your_message), Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(message: String?) {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.supportFragment, true)
                .build()
            findNavController().navigate(R.id.homeFragment,null, navOptions)
        }
    }

}