package com.dentify.dentify.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dentify.dentify.databinding.FragmentNoInvitationBinding
import com.dentify.dentify.ui.fragments.BaseFragment


class NoInvitationFragment : BaseFragment() {

    private lateinit var binding: FragmentNoInvitationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoInvitationBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        clickActions()

        return binding.root
    }

    private fun clickActions(){
        binding.tvLoginText.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvContactEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            requireActivity().startActivity(intent)
        }
    }
}