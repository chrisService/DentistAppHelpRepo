package com.dentify.dentify.ui.fragments.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostPasswordResetCompleteRequest
import com.dentify.dentify.databinding.FragmentSetNewPasswordBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetNewPasswordFragment : BaseFragment() {
    lateinit var binding: FragmentSetNewPasswordBinding
    val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var cKey: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetNewPasswordBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        passwordResetComplete()
        populateEmailTextField()
        goToLoginScreen()
        ifKeyboardIsShown()
        viewTooltip()

        return binding.root
    }

    private fun populateEmailTextField(){
        if(arguments?.getString("c") != null){
            cKey = arguments?.getString("c")!!
            userId = arguments?.getString(Constants.ID)!!
        }
    }

    fun viewTooltip(){
        binding.ivTooltip.setOnClickListener {
            if(binding.tvTooltipText.isVisible){
                binding.tvTooltipText.visibility = View.GONE
            }else{
                binding.tvTooltipText.visibility = View.VISIBLE
            }
        }
    }

    private fun passwordResetComplete(){
        binding.btnResetPassword.setOnClickListener {
            if(binding.etNewPassword.text.isNullOrEmpty()){
                binding.etNewPassword.error = getString(R.string.required_field)
                binding.etNewPassword.requestFocus()
            }else if (binding.etRepeatPassword.text.isNullOrEmpty()){
                binding.etRepeatPassword.error = getString(R.string.required_field)
                binding.etRepeatPassword.requestFocus()
            }else{
                val body = PostPasswordResetCompleteRequest()
                body.id = userId
                body.password = binding.etNewPassword.text.toString()
                body.confirmPassword = binding.etRepeatPassword.text.toString()
                body.passwordRecoveryCode = cKey

                viewModel.postResetPasswordComplete(body, object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        openPasswordResetFragment()
                    }
                    override fun onFailure(message: String?) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }


    fun openPasswordResetFragment(){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, false)
                .build()
            findNavController().navigate(R.id.passwordResetFragment, null, navOptions)
    }

    private fun goToLoginScreen(){
        binding.tvLogin.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setNewPasswordFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }
}