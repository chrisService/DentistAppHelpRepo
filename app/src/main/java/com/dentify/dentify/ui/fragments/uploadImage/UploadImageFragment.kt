package com.dentify.dentify.ui.fragments.uploadImage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.CollectionChangeListener
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.MUploadedFile
import com.dentify.dentify.databinding.FragmentUploadImageBinding
import com.dentify.dentify.databinding.PopupAddPhotoBinding
import com.dentify.dentify.databinding.PopupPdfBinding
import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.ContentResolverUtil.getFileName
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class UploadImageFragment : BaseFragment() {

    lateinit var binding: FragmentUploadImageBinding
    val images = mutableListOf<MUploadedFile>()
    private lateinit var pickPhotoUri: Uri
    lateinit var adapter: UploadImageRVAdapter
    private lateinit var mLastPhoto: Uri
    private lateinit var ivBlur: ImageView
    private lateinit var ivGreyTransparentOverlay: ImageView
    private var isPopupShown = false
    private var isImage: Boolean = false

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadImageBinding.inflate(layoutInflater)
        ivBlur = binding.ivBlur
        ivGreyTransparentOverlay = binding.ivGreyTransparentOverlay
        showLanguageSpinner(false)
        uiLogic()
        setAdapter()
        topBackButton()
        doOnBackPressed()
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding = FragmentUploadImageBinding.inflate(layoutInflater)
    }

    private fun uiLogic() {

        binding.btnTakePhoto.setOnClickListener {
            isImage = true
            loadImageFromCamera()
        }

        binding.btnOpenGalery.setOnClickListener {
            isImage = true
            loadImageFromGallery()
        }

        binding.btnOpenFiles.setOnClickListener {
            loadFileFromStorage()
        }

        binding.btnAddFile.setOnClickListener {
            chooseSourcePopup()
        }

        binding.btnContinue.setOnClickListener {
            StorageWrapper.saveAppointmentImages(images, requireContext())
            images.clear()
            findNavController().navigate(R.id.appointmentSummaryFragment)
        }

        binding.btnSkip.setOnClickListener {
            StorageWrapper.clearAppointmentImages(requireContext())
            if (images.isNotEmpty()) {
                for (item in images) {
                    deleteUploadedPhoto(item.imageId!!)
                }
            }
            findNavController().navigate(R.id.appointmentSummaryFragment)
        }
    }

    private fun setAdapter() {

        adapter = UploadImageRVAdapter(images, object : ViewModelClickListener<MUploadedFile> {
            override fun onClick(item: MUploadedFile) {
                if (item.fileName?.substringAfterLast(".")!!.contains("pdf")){
                    showPdfFromUri(item.imageUri!!, false)
                }else{
                    showPdfFromUri(item.imageUri!!, true)
                }
            }
        }, object : CollectionChangeListener {
            override fun onCollectionChanged(size: Int) {
                if (size > 0) {
                    binding.btnTakePhoto.isVisible = false
                    binding.btnOpenGalery.isVisible = false
                    binding.btnSkip.isVisible = false
                    binding.btnOpenFiles.isVisible = false
                    binding.btnContinue.isVisible = true
                    binding.btnAddFile.isVisible = true
                    binding.ivPhotoIllustration.isVisible = false
                    binding.clImageColage.isVisible = true
                } else {
                    binding.btnTakePhoto.isVisible = true
                    binding.btnOpenGalery.isVisible = true
                    binding.btnOpenFiles.isVisible = true
                    binding.btnSkip.isVisible = true
                    binding.btnContinue.isVisible = false
                    binding.btnAddFile.isVisible = false
                    binding.ivPhotoIllustration.isVisible = true
                    binding.clImageColage.isVisible = false
                }
            }
        }, object : ViewModelClickListener<MUploadedFile> {
            override fun onClick(item: MUploadedFile) {
                deleteUploadedPhoto(item.imageId!!)
            }
        }, requireContext())
        binding.rvImages.adapter = adapter


        val savedUploadedImages = StorageWrapper.getAppointmentImages(requireContext())
        if (!savedUploadedImages.isNullOrEmpty()) {
            for (item in savedUploadedImages) {
                images.add(item)
                adapter.cListener.onCollectionChanged(adapter.files.size)
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            pickPhotoUri = mLastPhoto
            binding.root.post {
                uploadPhoto1(pickPhotoUri)
            }
            mDismissPopup()
        }
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it != null) {
                pickPhotoUri = it
                binding.root.post {
                    uploadPhoto1(pickPhotoUri)
                }
            }
            mDismissPopup()
        })

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            startPhotoTaker()
        }

    private val requestGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            getImage.launch("image/*")
        }

    private val requestFilesPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            getImage.launch("*/*")
        }

    private fun startPhotoTaker() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mLastPhoto = FileProvider.getUriForFile(
            requireContext(),
            requireContext().getApplicationContext().getPackageName() + ".provider",
            createImageFile()
        )
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            mLastPhoto
        )
        // start the image capture Intent
        startActivityForResult(intent, 10001)
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


    private fun uploadPhoto1(photoUri: Uri) {
        try {
            val parcelFileDescriptor =
                requireContext().contentResolver.openFileDescriptor(photoUri, "r", null)
            val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
            val file = File(
                requireContext().filesDir,
                requireContext().contentResolver.getFileName(photoUri)
            )
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            val part: MultipartBody.Part
            if (isImage == true){
                part = MultipartBody.Part.createFormData(
                    "file", file.name, RequestBody.create(
                        "image/jpeg".toMediaTypeOrNull(),
                        file
                    )
                )
            }else{
                part = MultipartBody.Part.createFormData(
                    "file", file.name, RequestBody.create(
                        "*/*".toMediaTypeOrNull(),
                        file
                    )
                )
            }
            val fileSize = file.length().toInt()
            val fileSizeInKB = fileSize / 1024
            val fileSizeInMB = fileSizeInKB/ 1024
            if (fileSizeInMB < 10){
                viewModel.postUploadImage("AppointmentAttachment", part, object : ViewModelApiListener {
                    override fun onStarted(message: String?) {
                        binding.tvFileName.text = file.name.substringBeforeLast(".")
                        binding.tvExtension.text = ".${file.name.substringAfterLast(".")}"
                        binding.clDummyFile.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.uploading_photos),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSuccess(message: String?) {
                        binding.clDummyFile.visibility = View.GONE
                        images.add(
                            MUploadedFile(
                                file.name,
                                viewModel.uploadResponse.file?.uri!!,
                                viewModel.uploadResponse.file?.id!!
                            )
                        )
                        adapter.cListener.onCollectionChanged(adapter.files.size)
                        adapter.notifyDataSetChanged()
                        binding.btnContinue.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.image_uploaded),
                            Toast.LENGTH_SHORT
                        ).show()
                        isImage = false
                    }

                    override fun onFailure(message: String?) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(requireContext(), getString(R.string.file_exceeds_mb), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                getString(R.string.image_failed_to_upload),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteUploadedPhoto(imageId: String) {
        viewModel.deleteUploadedImage(imageId, object : ViewModelApiListener {
            override fun onStarted(message: String?) {
            }

            override fun onSuccess(message: String?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.image_has_been_removed),
                    Toast.LENGTH_SHORT
                ).show()
                images.removeAll { it.imageId == imageId }
                adapter.cListener.onCollectionChanged(adapter.files.size)
                adapter.notifyDataSetChanged()
                StorageWrapper.saveAppointmentImages(images, requireContext())
            }

            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun chooseSourcePopup() {
        isPopupShown = true
        val popupView = PopupAddPhotoBinding.inflate(layoutInflater)
        binding.ivPhotoIllustration.visibility = View.VISIBLE
        binding.clImageColage.visibility = View.GONE
        ivGreyTransparentOverlay.visibility = View.VISIBLE
        ivGreyTransparentOverlay.post {
            val bitmap = Blurry.with(requireContext())
                .radius(10)
                .sampling(8)
                .capture(binding.root).get()
            ivBlur.setImageDrawable(BitmapDrawable(resources, bitmap))
            ivBlur.visibility = View.VISIBLE
        }
        popupView.btnOpenGalery.setOnClickListener {
            isImage = true
            loadImageFromGallery()
        }
        popupView.btnTakePhoto.setOnClickListener {
            isImage = true
            loadImageFromCamera()
        }
        popupView.btnOpenFiles.setOnClickListener {
            loadFileFromStorage()
        }
        popupView.popupHalo.setOnClickListener {
            mDismissPopup()
        }

        showPopup(popupView.root, binding.root)
        popup?.animationStyle = R.style.Animation
    }

    private fun showPdfFromUri(url: String, image:Boolean) {
        val popupView = PopupPdfBinding.inflate(layoutInflater)
        val web = popupView.webView
        web.settings.javaScriptEnabled = true
        web.settings.builtInZoomControls = true
        web.settings.displayZoomControls = false
        web.settings.loadWithOverviewMode = true
        web.settings.useWideViewPort = true
        web.settings.domStorageEnabled = true
        if (image){
            web.loadUrl(url)
        }else{
            web.loadUrl("https://docs.google.com/gview?embedded=true&url="+url)
            web.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {}
            }
        }
        popupView.popupHalo.setOnClickListener {
            dismissPopup()
        }
        showPopup(popupView.root, binding.root)
    }

    private fun loadImageFromCamera() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun loadImageFromGallery() {
        Log.d("navFlow", "navFlow loadImageFromGallery: initiate false")
        requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun loadFileFromStorage() {
        Log.d("navFlow", "navFlow loadImageFromGallery: initiate false")
        requestFilesPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun mDismissPopup(){
        isPopupShown = false
        dismissPopup()
        binding.ivPhotoIllustration.visibility = View.GONE
        binding.clImageColage.visibility = View.VISIBLE
        ivGreyTransparentOverlay.visibility = View.GONE
        ivBlur.visibility = View.GONE
    }

    private fun topBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun doOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isPopupShown == true){
                mDismissPopup()
            }else{
                if (images.isNotEmpty()) {
                    StorageWrapper.saveAppointmentImages(images, requireContext())
                    findNavController().popBackStack(R.id.reasonForAppointmentFragment, false)
                } else {
                    findNavController().popBackStack(R.id.reasonForAppointmentFragment, false)
                }
            }
        }
    }
}