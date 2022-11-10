package com.dentify.dentify.ui.fragments.signUp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.BuildConfig
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.databinding.FragmentVerifyPersonalNumberBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyPersonalNumberFragment : BaseFragment() {
    lateinit var binding: FragmentVerifyPersonalNumberBinding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var invitationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyPersonalNumberBinding.inflate(layoutInflater)

        populateEmailTextField()
        btnVerify()
        showLanguageSpinner(true)
        doOnBackPressed()
        goToLogin()

        return binding.root
    }

    private fun populateEmailTextField(){
        if(arguments?.getString(Constants.ID) != null){
            Log.d("signUp flow", "populateEmailTextField: initialized ID")
            invitationId = arguments?.getString(Constants.ID)!!
            StorageWrapper.invitationId = invitationId
        }else{
            invitationId = ""
        }
    }

    private fun goToLogin(){
        binding.tvLogin.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }

    private fun btnVerify(){
        binding.btnVerify.setOnClickListener {
            postBankIdAuth()
        }
    }

    fun openBankIdApp(autoStartToken: String){
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        if(BuildConfig.FLAVOR == "stage"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/verify-stage")
        }else if (BuildConfig.FLAVOR == "dev"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/verify-dev")
        }else if(BuildConfig.FLAVOR == "prod"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/verify")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun postBankIdAuth(){
        if (invitationId.isNotEmpty()){
            viewModel.getBankIdAuth( object: ViewModelApiListener {
                override fun onStarted(message: String?) {
                    binding.progressBar.isVisible = true
                }
                override fun onSuccess(message: String?) {
                    StorageWrapper.orderRef = viewModel.authResponse.orderRef
                    openBankIdApp(viewModel.authResponse.autoStartToken!!)
                }
                override fun onFailure(message: String?) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                }
            })
        }
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }
}
