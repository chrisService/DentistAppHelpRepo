package com.dentify.dentify.ui.fragments.login


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.BuildConfig
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostTokenBankIdRequest
import com.dentify.dentify.databinding.FragmentLoginBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loadingView: View
    private var isLoadingShown: Boolean = false
    private lateinit var videoView: VideoView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        lifecycle.addObserver(MainActivityLifeObserver())
        videoView = binding.vvLoginVideo

        return binding.root
    }

    private fun playLoginVideo(){
        val videoUri = Uri.parse(Constants.ANDROID_RESOURCES + requireContext().applicationContext.packageName + "/" + R.raw.login_screen_video)
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            videoView.start()
            it.isLooping = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        showLanguageSpinner(true)
        buttonClicks()
        doOnBackPressed()
        postBankIdAuthToken()
        Log.d("Created", "onViewCreated: finished")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding = FragmentLoginBinding.inflate(layoutInflater)
    }

    private fun initializeView() {
        loadingView = LoadingScreen.loadingScreen(requireContext(), binding.root)
        languageSpinner = requireActivity().findViewById<Spinner>(R.id.spinner_language)
    }

    fun addLoadingView(){
        if(!isLoadingShown){
            binding.root.addView(loadingView)
            isLoadingShown = true
        }
    }

    fun removeLoadingView(){
        binding.root.removeView(loadingView)
        isLoadingShown = false
    }

    private fun buttonClicks() {
        binding.tvLoginWithEmail.setOnClickListener{
            findNavController().navigate(R.id.loginWithEmailFragment)
        }

        binding.btnLoginWithBankId.setOnClickListener {
            if (isSwedishMobiltBankIdInstalled(requireContext())){
                languageSpinner.visibility = View.GONE
                getBankIdAuth()
            }else{
                Toast.makeText(requireContext(), getString(R.string.bankid_not_installed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSwedishMobiltBankIdInstalled(context: Context): Boolean {
        val pm: PackageManager = context.getPackageManager()
        return try {
            pm.getPackageInfo("com.bankid.bus", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getPatientsProfile(){
        viewModel.getProfile(requireContext(), object: ViewModelApiListener{
            override fun onStarted(message: String?) {
                addLoadingView()
            }
            override fun onSuccess(message: String?) {
                getSupportReason()
                WelcomeScreen.showWelcomeScreen(requireContext(), this@LoginFragment, binding.root)
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
                if (!viewModel.supportReasonsResponse.reasons.isNullOrEmpty()){
                    StorageWrapper.saveSupportReason(viewModel.supportReasonsResponse.reasons, requireContext())
                }
                subscribeDevice()
            }
            override fun onFailure(message: String?) {
                removeLoadingView()
            }
        })
    }

    private fun subscribeDevice(){
        viewModel.postDevice(object: ViewModelApiListener{
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

    private fun getAppointments(){
        viewModel.getAppointments(requireContext(), object: ViewModelApiListener{
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

    private fun getBankIdAuth(){
        try {
            viewModel.getBankIdAuth( object: ViewModelApiListener {
                override fun onStarted(message: String?) {
                    addLoadingView()
                }
                override fun onSuccess(message: String?) {
                    StorageWrapper.orderRef = viewModel.authResponse.orderRef
                    openBankIdApp(viewModel.authResponse.autoStartToken!!)
                }
                override fun onFailure(message: String?) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch (e: Exception){
            Toast.makeText(requireContext(), "Network problem, check your internet connection", Toast.LENGTH_SHORT).show()
            Log.e("Connection Error", e.message.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        playLoginVideo()
    }

    private fun openBankIdApp(autoStartToken: String){
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        if(BuildConfig.FLAVOR == "stage"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/bankid-stage")
        }else if (BuildConfig.FLAVOR == "dev"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/bankid-dev")
        }else if(BuildConfig.FLAVOR == "prod"){
            intent.data = Uri.parse("https://app.bankid.com/?autostarttoken=$autoStartToken&redirect=https://dentify.page.link/bankid")
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun postBankIdAuthToken(){
        if (!StorageWrapper.orderRef.isNullOrEmpty()){

            languageSpinner.visibility = View.GONE

            val body = PostTokenBankIdRequest()
            body.orderRef = StorageWrapper.orderRef

            viewModel.postTokenBankId(body, object: ViewModelApiListener{
                override fun onStarted(message: String?) {
                    addLoadingView()
                }
                override fun onSuccess(message: String?) {
                    StorageWrapper.orderRef = null
                    getPatientsProfile()
                }
                override fun onFailure(message: String?) {
                    if(message == Constants.INVALID_CREDENCIALS){
                        findNavController().navigate(R.id.noInvitationFragment)
                    }
                    StorageWrapper.orderRef = null
                    removeLoadingView()
                }
            })
        }
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }
    }
}
