package com.dentify.dentify.ui.fragments.settings

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.BaseApplication
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostDeactivationRequest
import com.dentify.dentify.databinding.FragmentSettingsBinding
import com.dentify.dentify.databinding.PopupDeactivateConfirmBinding
import com.dentify.dentify.databinding.PopupDeactivateInfoBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.HardcodeValues
import com.dentify.dentify.util.LogoutHandler
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import jp.wasabeef.blurry.Blurry

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var ivBlur: ImageView
    private lateinit var ivGreyTransparentOverlay: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        showLanguageSpinner(false)
        initializeView()
        clickActions()
        doOnBackPressed()

        return binding.root
    }

    private fun initializeView(){
        if (BaseApplication.instance!!.getLanguagePref() == Constants.ENGLISH){
            binding.tvSelectedLanguage.text = Constants.ENGLISH_LANGUAGE
        }else{
            binding.tvSelectedLanguage.text = Constants.SWEDISH_LANGUAGE
        }
    }

    private fun clickActions(){
        binding.llClickArea.setOnClickListener {
            findNavController().navigate(R.id.displayLanguageFragment)
        }
        binding.llClickArea01.setOnClickListener {
            openInfoPopup()
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun openInfoPopup(){
        val popupView = PopupDeactivateInfoBinding.inflate(layoutInflater)
        ivBlur = binding.ivBlur
        ivGreyTransparentOverlay = binding.ivGreyTransparentOverlay
        ivGreyTransparentOverlay.visibility = View.VISIBLE
        ivGreyTransparentOverlay.post {
            val bitmap = Blurry.with(requireContext())
                .radius(10)
                .sampling(8)
                .capture(binding.root).get()
            ivBlur.setImageDrawable(BitmapDrawable(resources, bitmap))
            ivBlur.visibility = View.VISIBLE
        }
        popupView.popupHalo.setOnClickListener {
            dismissPopup()
            ivBlur.visibility = View.GONE
            ivGreyTransparentOverlay.visibility = View.GONE
        }
        popupView.btnNotNow.setOnClickListener{
            dismissPopup()
            ivBlur.visibility = View.GONE
            ivGreyTransparentOverlay.visibility = View.GONE
        }
        popupView.btnIAmSure.setOnClickListener{
            dismissPopup()
            openDeactivatePopup()
        }
        val markwon = Markwon.create(requireContext())
        if(StorageWrapper.selectedLocale == Constants.SWEDISH){
            markwon.setMarkdown(popupView.tvInfoText, HardcodeValues.getTextPathDeactivationSV(requireContext()))
        }else {
            markwon.setMarkdown(popupView.tvInfoText, HardcodeValues.getTextPathDeactivationEN(requireContext()))
        }
        showPopup(popupView.root, binding.root)
        popup?.animationStyle = R.style.Animation
    }

    fun openDeactivatePopup(){
        val popupView = PopupDeactivateConfirmBinding.inflate(layoutInflater)

        val markwon = Markwon.create(requireContext())
        if(StorageWrapper.selectedLocale == Constants.SWEDISH){
            markwon.setMarkdown(popupView.tvDeactivateText, HardcodeValues.getTextPathDeactivationConfirmSV())
        }else {
            markwon.setMarkdown(popupView.tvDeactivateText, HardcodeValues.getTextPathDeactivationConfirmEN())
        }

        popupView.popupHalo.setOnClickListener {
            dismissPopup()
            ivBlur.visibility = View.GONE
            ivGreyTransparentOverlay.visibility = View.GONE
        }

        popupView.btnDiscardPopup.setOnClickListener {
            dismissPopup()
            ivBlur.visibility = View.GONE
            ivGreyTransparentOverlay.visibility = View.GONE
        }

        popupView.btnConfirmPopup.setOnClickListener {
            if (popupView.etDeactivate.text.toString().equals(Constants.DEACTIVATE, false)){
                val body = PostDeactivationRequest()
                body.confirmationText = popupView.etDeactivate.text.toString()
                viewModel.postDeactivation(body, object: ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                        dismissPopup()
                        ivBlur.visibility = View.GONE
                        ivGreyTransparentOverlay.visibility = View.GONE
                        LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
                    }
                    override fun onFailure(message: String?) {
                    }
                })
            }else{
                Toast.makeText(requireContext(), getString(R.string.type_the_word_above), Toast.LENGTH_SHORT).show()
            }
        }
        showPopup(popupView.root, binding.root)
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            findNavController().navigate(R.id.profileFragment,null, navOptions)
        }
    }
}