package com.dentify.dentify.ui.fragments.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostPasswordResetBeginRequest
import com.dentify.dentify.databinding.FragmentForgotPasswordBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment() {
    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        openCheckEmailFragment()
        backToLogin()
        ifKeyboardIsShown()

        return binding.root
    }

    private fun backToLogin(){
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment, false)
        }
    }

    private fun openCheckEmailFragment(){
        binding.btnResetPassword.setOnClickListener {
            postPasswordReset()
        }
    }

    private fun postPasswordReset(){

        val body = PostPasswordResetBeginRequest()
        body.email = binding.editTextEmail.text.toString()

        viewModel.postResetPassword(body, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                StorageWrapper.resetPasswordEmail = binding.editTextEmail.text.toString()
                findNavController().navigate(R.id.checkYourEmailFragment)
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}