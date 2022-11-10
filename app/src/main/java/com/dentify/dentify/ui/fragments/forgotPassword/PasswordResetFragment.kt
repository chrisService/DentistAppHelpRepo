package com.dentify.dentify.ui.fragments.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.databinding.FragmentPasswordResetBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import io.noties.markwon.Markwon


class PasswordResetFragment : BaseFragment() {
    lateinit var binding: FragmentPasswordResetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordResetBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        setView()
        goToLoginScreen()

        return binding.root
    }

    private fun setView(){
        val markwon = Markwon.create(requireContext())
        markwon.setMarkdown(binding.tvContinueText, getString(R.string.tap_on_quot_continue_quot_to_log_in_with_your_new_credentials))
    }

    private fun goToLoginScreen(){
        binding.btnProceed.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, false)
                .build()
            findNavController().navigate(R.id.loginWithEmailFragment, null, navOptions)
        }
    }

}