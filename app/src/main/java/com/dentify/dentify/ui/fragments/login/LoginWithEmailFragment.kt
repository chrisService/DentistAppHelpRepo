package com.dentify.dentify.ui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostAuthTokenRequest
import com.dentify.dentify.databinding.FragmentLoginWithEmailBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.InputValidator.isRequiredFieldNotEmpty
import com.dentify.dentify.util.InputValidator.isValidEmailAddress
import com.dentify.dentify.util.LoadingScreen
import com.dentify.dentify.util.MessageUtil.shortToast
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginWithEmailFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginWithEmailBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loadingView: View
    private var isLoadingShown: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginWithEmailBinding.inflate(layoutInflater)
        showLanguageSpinner(true)
        populateEtEmail()
        ifKeyboardIsShown()
        doOnBackPressed()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        buttonClicks()
    }

    fun setHintsProperLanguage() {
        binding.etEmailLayout.hint = getString(R.string.email)
        binding.etPasswordLayout.hint = getString(R.string.password)
    }

    private fun populateEtEmail() {
        if (!StorageWrapper.profileEmail.isNullOrEmpty()) {
            binding.editTextEmail.setText(StorageWrapper.profileEmail)
        }
    }

    private fun buttonClicks() {
        topBackButtom()
        binding.btnOpenApp.setOnClickListener {
            if (binding.editTextEmail.isRequiredFieldNotEmpty() && binding.editTextPassword.isRequiredFieldNotEmpty()) {
                if (binding.editTextEmail.isValidEmailAddress()) {
                    getToken()
                    languageSpinner.visibility = View.GONE
                } else {
                    context?.shortToast(getString(R.string.email_validation_message))
                }
            } else {
                context?.shortToast(getString(R.string.empty_fields_user_and_pass_validation_message))
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }
    }

    private fun initializeView() {
        loadingView = LoadingScreen.loadingScreen(requireContext(), binding.root)
        languageSpinner = requireActivity().findViewById<Spinner>(R.id.spinner_language)
        ifKeyboardIsShown()
    }

    private fun topBackButtom() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun addLoadingView() {
        if (!isLoadingShown) {
            binding.root.addView(loadingView)
            isLoadingShown = true
        }
    }

    fun removeLoadingView() {
        binding.root.removeView(loadingView)
        isLoadingShown = false
    }

    private fun getToken() {
        val body = PostAuthTokenRequest()
        body.grantType = Constants.PASSWORD
        body.email = binding.editTextEmail.text.toString()
        body.password = binding.editTextPassword.text.toString()

        try {
            viewModel.postToken(body, object : ViewModelApiListener {
                override fun onStarted(message: String?) {
                    addLoadingView()
                }

                override fun onSuccess(message: String?) {
                    getPatientsProfile()
                }

                override fun onFailure(message: String?) {
                    binding.root.removeView(loadingView)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch (e: Exception){
            Toast.makeText(requireContext(), "Network problem, check your internet connection", Toast.LENGTH_SHORT).show()
            Log.e("Connection Error", e.message.toString())
        }
    }

    private fun getPatientsProfile() {
        viewModel.getProfile(requireContext(), object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                addLoadingView()
            }

            override fun onSuccess(message: String?) {
                getSupportReason()
                WelcomeScreen.showWelcomeScreen(requireContext(), this@LoginWithEmailFragment, binding.root)
            }

            override fun onFailure(message: String?) {
                StorageWrapper.clearData(requireContext())
                removeLoadingView()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSupportReason() {
        viewModel.getSupportReasons(object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                addLoadingView()
            }

            override fun onSuccess(message: String?) {
                if (!viewModel.supportReasonsResponse.reasons.isNullOrEmpty()) {
                    StorageWrapper.saveSupportReason(
                        viewModel.supportReasonsResponse.reasons,
                        requireContext()
                    )
                }
                subscribeDevice()
            }

            override fun onFailure(message: String?) {
                removeLoadingView()
            }
        })
    }

    private fun subscribeDevice() {
        viewModel.postDevice(object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                addLoadingView()
            }

            override fun onSuccess(message: String?) {
                getAppointments()
            }

            override fun onFailure(message: String?) {
                removeLoadingView()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                Log.e("ApiFailed", "postDevice: $message")
            }
        })
    }

    private fun getAppointments() {
        viewModel.getAppointments(requireContext(), object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                StorageWrapper.clearAppointments(requireContext())
                addLoadingView()
            }

            override fun onSuccess(message: String?) {
                StorageWrapper.orderRef = null
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                findNavController().navigate(R.id.homeFragment, null, navOptions)
                removeLoadingView()
            }

            override fun onFailure(message: String?) {
                removeLoadingView()
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}