package com.dentify.dentify.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.dentify.dentify.BaseApplication
import com.dentify.dentify.R
import com.dentify.dentify.databinding.FragmentDisplayLanguageBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.Constants.ENGLISH
import com.dentify.dentify.util.Constants.SWEDISH
import com.dentify.dentify.util.LocaleManager
import com.dentify.dentify.util.StorageWrapper


class DisplayLanguageFragment : BaseFragment() {

    private lateinit var binding: FragmentDisplayLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayLanguageBinding.inflate(layoutInflater)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        initializeView()
        showLanguageSpinner(false)
        clickActions()

        return binding.root
    }

    private fun initializeView(){
        binding.rbSwedish.text = Constants.SWEDISH_LANGUAGE
        binding.rbEnglish.text = Constants.ENGLISH_LANGUAGE
        if (BaseApplication.instance!!.getLanguagePref() == ENGLISH){
            binding.rgLang.check(R.id.rbEnglish)
        }else{
            binding.rgLang.check(R.id.rbSwedish)
        }
    }

    private fun clickActions(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.rbSwedish.setOnClickListener {
            LocaleManager.setNewLocale(requireContext(), SWEDISH)
            StorageWrapper.selectedLocale = SWEDISH
            requireActivity().recreate()
        }
        binding.rbEnglish.setOnClickListener {
            LocaleManager.setNewLocale(requireContext(), ENGLISH)
            StorageWrapper.selectedLocale = ENGLISH
            requireActivity().recreate()
        }
    }

}