package com.dentify.dentify.ui.fragments.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.databinding.FragmentSplashBinding
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.LogoutHandler
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    private lateinit var bindings: FragmentSplashBinding
    val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentSplashBinding.inflate(layoutInflater)

        doOnBackPressed()
        fadeText()

        return bindings.root
    }

    private fun fadeText() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = Constants.SPLASH_SCREEN_FADE_TIME

            val animation = AnimationSet(false)
            animation.addAnimation(fadeIn)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }
                override fun onAnimationEnd(animation: Animation?) {
                    if (!StorageWrapper.accessToken.isNullOrEmpty()) {
                        getPatientsProfile()
                    } else {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.splashFragment, true)
                            .build()
                        findNavController().navigate(R.id.loginFragment, null, navOptions)
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            if (StorageWrapper.splashText == Constants.SPLASH_TEXT_1) {
                bindings.tvPopupText.text = getString(R.string.pain_free_dentistry_made_easy)
                StorageWrapper.splashText = Constants.SPLASH_TEXT_2
            } else {
                bindings.tvPopupText.text =
                    getString(R.string.meeting_a_dentist_doesn_t_need_to_be_scary)
                StorageWrapper.splashText = Constants.SPLASH_TEXT_1
            }
            bindings.tvPopupText.startAnimation(animation)
            bindings.tvPopupText.visibility = View.VISIBLE

        }, 500)
    }

    private fun getPatientsProfile() {
        try {
            viewModel.getProfile(requireContext(), object : ViewModelApiListener {
                override fun onStarted(message: String?) {
                }

                override fun onSuccess(message: String?) {
                    getSupportReason()
                    WelcomeScreen.showWelcomeScreen(requireContext(), this@SplashFragment, bindings.root)
                }

                override fun onFailure(message: String?) {
                    LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch (e: Exception){
            Toast.makeText(requireContext(), "Network problem, check your internet connection", Toast.LENGTH_SHORT).show()
            Log.e("Connection Error", e.message.toString())
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
    }

    private fun getSupportReason() {
        viewModel.getSupportReasons(object : ViewModelApiListener {
            override fun onStarted(message: String?) {
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
                LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
            }
        })
    }

    private fun subscribeDevice() {
        viewModel.postDevice(object : ViewModelApiListener {
            override fun onStarted(message: String?) {
            }

            override fun onSuccess(message: String?) {
                getAppointments()
            }

            override fun onFailure(message: String?) {
                LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                Log.e("ApiFailed", "postDevice: $message")
            }
        })
    }

    private fun getAppointments() {
        viewModel.getAppointments(requireContext(), object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                StorageWrapper.clearAppointments(requireContext())
            }

            override fun onSuccess(message: String?) {
                StorageWrapper.orderRef = null
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build()
                findNavController().navigate(R.id.homeFragment, null, navOptions)
            }

            override fun onFailure(message: String?) {
                LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }
}