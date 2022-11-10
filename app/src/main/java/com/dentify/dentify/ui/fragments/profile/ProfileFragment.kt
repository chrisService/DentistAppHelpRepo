package com.dentify.dentify.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PutPatientProfileRequest
import com.dentify.dentify.apiModel.response.GetPatientsProfileResponse
import com.dentify.dentify.databinding.FragmentProfileBinding
import com.dentify.dentify.databinding.PopupAddProfilePhotoBinding
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.*
import com.dentify.dentify.util.ContentResolverUtil.getFileName
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    lateinit var binding: FragmentProfileBinding
    private var pickPhotoUri: Uri? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var popupImage: ImageView
    private lateinit var popupRl: RelativeLayout
    private lateinit var ivBlur: ImageView
    private lateinit var ivGreyTransparentOverlay: ImageView
    private lateinit var mLastPhoto: Uri
    lateinit var profile: GetPatientsProfileResponse
    val mRequestCode = 10001
    var isNewPictureAdded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        setText()
        logout()
        imageButtonClic()
        updateProfileClick()
        openSettings()
        doOnBackPressed()

        return binding.root
    }

    private fun setText(){
        profile = StorageWrapper.getPatientsProfileResponse(requireContext())!!
        binding.etName.setText(profile.patient?.fullName)
        binding.editTextEmail.setText(profile.patient?.emailAddress)
        binding.etPhone.setText(profile.patient?.phoneNumber)
        binding.tvInitials.text = Util.initials(profile.patient?.fullName)
        pickPhotoUri = Uri.parse(StorageWrapper.profilePictureUri)
        if (pickPhotoUri.toString().isNotEmpty()){
            binding.ivProfilePic.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            binding.rlProfilePicture.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }

    private fun openSettings(){
        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
    }

    private fun imageButtonClic(){
        binding.btnUploadProfilePic.setOnClickListener {
            chooseSourcePopup()
        }
    }

    private fun updateProfileClick(){


        binding.btnSaveChanges.setOnClickListener {
            if(isNewPictureAdded == true){
                uploadPhoto1()
            }else{
                updateProfile()
            }
        }
    }

    private fun logout(){
        binding.btnLogout.setOnClickListener {
            LogoutHandler.unsubscribeAndLogout(requireContext(), viewModel, findNavController())
        }
    }

    private fun loadImageFromCamera() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun loadImageFromGallery() {
        requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun chooseSourcePopup(){
        val popupView = PopupAddProfilePhotoBinding.inflate(layoutInflater)
        popupImage = popupView.ivProfilePic
        popupRl = popupView.rlProfilePicture
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
        if (pickPhotoUri.toString().isNotEmpty()){
            popupImage.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            popupRl.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }else{
            popupView.btnRemovePhoto.visibility = View.GONE
        }
        popupView.btnOpenGalery.setOnClickListener {
            loadImageFromGallery()
        }
        popupView.btnTakePhoto.setOnClickListener {
            loadImageFromCamera()
        }
        popupView.btnRemovePhoto.setOnClickListener {
            StorageWrapper.profilePictureID = null
            pickPhotoUri = null
            isNewPictureAdded = false
            binding.ivProfilePic.setImageDrawable(null)
            popupImage.setImageDrawable(null)
            binding.rlProfilePicture.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.profilePicInitialsBackground)
        }
        popupView.popupHalo.setOnClickListener {
            dismissPopup()
            ivBlur.visibility = View.GONE
            ivGreyTransparentOverlay.visibility = View.GONE
        }

        showPopup(popupView.root, binding.root)
        popup?.animationStyle = R.style.Animation
    }

    val handler = Handler(Looper.getMainLooper())

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it != null){
                pickPhotoUri = it
                popupImage.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
                popupRl.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                binding.root.post{
                    isNewPictureAdded = true
                    binding.ivProfilePic.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
                    binding.rlProfilePicture.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    handler.postDelayed({
                        dismissPopup()
                        ivBlur.visibility = View.GONE
                        ivGreyTransparentOverlay.visibility = View.GONE
                    }, 1000)
                }
            }
        })

    //TODO Do not use deprecated functions. There is a new way of doing it now:
    // https://stackoverflow.com/a/70486231/7781634
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mRequestCode && resultCode == Activity.RESULT_OK){
            pickPhotoUri = mLastPhoto
            popupImage.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
            popupRl.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            binding.root.post{
                isNewPictureAdded = true
                binding.ivProfilePic.loadCircularImage(pickPhotoUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
                binding.rlProfilePicture.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
                handler.postDelayed({
                    dismissPopup()
                    ivBlur.visibility = View.GONE
                    ivGreyTransparentOverlay.visibility = View.GONE
                }, 1000)
            }
        }
    }

    private val requestGalleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        getImage.launch("image/*")
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        startPhotoTaker()
    }

    fun startPhotoTaker() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mLastPhoto = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", createImageFile())
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            mLastPhoto
        )
        // start the image capture Intent
        startActivityForResult(intent, mRequestCode)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun uploadPhoto1(){

        try{
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(pickPhotoUri!!, "r", null)

            val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
            val file = File(requireContext().filesDir, requireContext().contentResolver.getFileName(pickPhotoUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            val part = MultipartBody.Part.createFormData(
                "file", file.name, RequestBody.create(
                    "image/jpeg".toMediaTypeOrNull(),
                    file
                )
            )

            viewModel.postUploadImage("ProfileImage", part, object: ViewModelApiListener {
                override fun onStarted(message: String?) {
                    binding.progressBar.isVisible = true
                }

                override fun onSuccess(message: String?) {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.image_uploaded), Toast.LENGTH_SHORT).show()
                    StorageWrapper.profilePictureUri = viewModel.uploadResponse.file?.uri
                    StorageWrapper.profilePictureID = viewModel.uploadResponse.file?.id
                    updateProfile()
                }

                override fun onFailure(message: String?) {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("upload image error", "uploadPhoto1: " + e.message)
            Toast.makeText(requireContext(), getString(R.string.image_failed_to_upload), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(){
        val body = PutPatientProfileRequest()
        body.fullName = binding.etName.text.toString()
        body.phoneNumber = binding.etPhone.text.toString()
        body.profileImageId = StorageWrapper.profilePictureID

        viewModel.updateProfile(body, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                isNewPictureAdded = false
                viewModel.getProfile(requireContext(), object:ViewModelApiListener{
                    override fun onStarted(message: String?) {
                    }
                    override fun onSuccess(message: String?) {
                    }
                    override fun onFailure(message: String?) {
                    }
                })
            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.profileFragment, true)
                .build()
            findNavController().navigate(R.id.homeFragment,null, navOptions)
        }
    }
}
