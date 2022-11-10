package com.dentify.dentify.ui.fragments.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.databinding.FragmentCheckYourEmailBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class CheckYourEmailFragment : BaseFragment() {
    lateinit var binding: FragmentCheckYourEmailBinding
    val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckYourEmailBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        setView()
        sendAgain()
        backToLogin()
        openSetNewPasswordFragment()

        return binding.root
    }

    private fun setView(){
        val markwon = Markwon.create(requireContext())
        val checkEmailTextString = "${getString(R.string.we_sent_a_password_reset_link_to)} **${StorageWrapper.resetPasswordEmail}**"
        markwon.setMarkdown(binding.tvEmail, checkEmailTextString)
    }

    private fun openSetNewPasswordFragment(){
        binding.btnResetPassword.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            requireActivity().startActivity(intent)
        }
    }

    private fun sendAgain(){
        binding.tvResend.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun backToLogin(){
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment, false)
        }
    }

}