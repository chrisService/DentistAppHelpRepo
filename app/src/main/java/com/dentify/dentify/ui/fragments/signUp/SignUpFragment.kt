package com.dentify.dentify.ui.fragments.signUp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.query.GetBankIdCollectQuery
import com.dentify.dentify.apiModel.request.PostPatientsPatientsRequest
import com.dentify.dentify.databinding.FragmentSignUpBinding
import com.dentify.dentify.databinding.PopupTermAndConditionsBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.HardcodeValues
import com.dentify.dentify.util.InputValidator.isRequiredFieldNotEmpty
import com.dentify.dentify.util.InputValidator.passwordsMatch
import com.dentify.dentify.util.MessageUtil.shortToast
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {

    private val viewModel: SignUpViewModel by viewModels()
    lateinit var binding: FragmentSignUpBinding
    private lateinit var etName: EditText
    private lateinit var etCreatePassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var etPhone: EditText
    lateinit var btnSignUp: Button
    private var invitationId: String? = StorageWrapper.invitationId
    lateinit var personalNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        initializeView()
        buttonClicks()
        showLanguageSpinner(true)
        getEmail(invitationId)
        bankIdCollect()
        doOnBackPressed()

        return binding.root
    }

    private fun initializeView(){
        etName = binding.etName
        etCreatePassword = binding.etCreatePassword
        etRepeatPassword = binding.etRepeatPassword
        etPhone = binding.etPhone
        btnSignUp = binding.btnSignUp
        ifKeyboardIsShown()
    }

    private fun buttonClicks(){
        binding.tvLogIn.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
        binding.checkBoxTermsAndConditions.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (!binding.checkBoxTermsAndConditions.isChecked){
                    btnSignUp.alpha = 0.5F
                    btnSignUp.isEnabled = false
                }else{
                    btnSignUp.alpha = 1F
                    btnSignUp.isEnabled = true
                }
            }
        })
        btnSignUp.setOnClickListener {
            if (etName.isRequiredFieldNotEmpty() && etPhone.isRequiredFieldNotEmpty() && etCreatePassword.isRequiredFieldNotEmpty() && etRepeatPassword.isRequiredFieldNotEmpty()) {
                if (passwordsMatch(
                        etCreatePassword.text.toString(),
                        etRepeatPassword.text.toString()
                    )
                ) {
                    if (!binding.editTextEmail.text.isNullOrEmpty() && ::personalNumber.isInitialized) {
                        postPatient()
                    } else {
                        showSignUpInfoAlertDialog()
                    }
                } else {
                    context?.shortToast(getString(R.string.passwords_dont_match_validation_message))
                }
            } else {
                context?.shortToast(getString(R.string.please_fill_all_fields))
            }
        }
        binding.tvTermsAndConditions.setOnClickListener {
            showTermsPopup()
        }
        binding.ivTooltip.setOnClickListener {
            viewTooltip()
        }
        binding.etCreatePassword.setOnClickListener {
            binding.tvTooltipText.visibility = View.GONE
        }
        binding.etRepeatPassword.setOnClickListener {
            binding.tvTooltipText.visibility = View.GONE
        }
    }

    fun viewTooltip(){
        if(binding.tvTooltipText.isVisible){
            binding.tvTooltipText.visibility = View.GONE
        }else{
            binding.tvTooltipText.visibility = View.VISIBLE
        }
    }

    private fun postPatient(){
        val body =  PostPatientsPatientsRequest()
        body.id = invitationId
        body.fullName = etName.text.toString()
        body.password = etCreatePassword.text.toString()
        body.passwordConfirmation = etRepeatPassword.text.toString()
        body.phoneNumber = etPhone.text.toString()
        body.personalNumber = personalNumber

        viewModel.postPatient(body, object: ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }
            override fun onSuccess(message: String?) {
                Toast.makeText(requireContext(), getString(R.string.registered), Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false
                StorageWrapper.orderRef = null
                StorageWrapper.invitationId = null
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                findNavController().navigate(R.id.loginFragment, null, navOptions)
            }
            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getEmail(id: String?){
        if(id != null){
            viewModel.getEmail(id, object: ViewModelApiListener{
                override fun onStarted(message: String?) {
                    binding.progressBar.isVisible = true
                }
                override fun onSuccess(message: String?) {
                    binding.progressBar.isVisible = false
                    binding.editTextEmail.setText(viewModel.emailResponse.emailAddress)
                    binding.etPhone.setText(viewModel.emailResponse.phoneNumber)
                }
                override fun onFailure(message: String?) {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun bankIdCollect(){
        if (!StorageWrapper.orderRef.isNullOrEmpty()){
            val query = GetBankIdCollectQuery()
            query.orderRef = StorageWrapper.orderRef!!
            viewModel.getBankIdCollect(query, object: ViewModelApiListener{
                override fun onStarted(message: String?) {
                    binding.progressBar.isVisible = true
                }
                override fun onSuccess(message: String?) {
                    StorageWrapper.orderRef = null
                    binding.progressBar.isVisible = false
                    binding.etName.setText(viewModel.bankIdCollectResponse.completitionData?.user?.name)
                    personalNumber = viewModel.bankIdCollectResponse.completitionData?.user?.personalNumber!!
                }
                override fun onFailure(message: String?) {
                    StorageWrapper.orderRef = null
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showSignUpInfoAlertDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage("To start using the Dentify App you need to register via the Dentify home page and receive an email invitation")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(
            context?.getString(android.R.string.ok)
        ) { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showTermsPopup() {
        val popupView = PopupTermAndConditionsBinding.inflate(layoutInflater)

        val markwon = Markwon.create(requireContext())

        if(StorageWrapper.selectedLocale == Constants.ENGLISH){
            markwon.setMarkdown(popupView.tvPopupTc, HardcodeValues.getTextPathEN(requireContext()))
        }else {
            markwon.setMarkdown(popupView.tvPopupTc, HardcodeValues.getTextPathSV(requireContext()))
        }
        popupView.popupHalo.setOnClickListener {
            dismissPopup()
        }

        showPopup(popupView.root, binding.root)
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }
}
