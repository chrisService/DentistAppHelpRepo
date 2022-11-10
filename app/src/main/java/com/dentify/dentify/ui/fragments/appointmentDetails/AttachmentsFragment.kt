package com.dentify.dentify.ui.fragments.appointmentDetails

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Attachment
import com.dentify.dentify.databinding.FragmentAttachmentsBinding
import com.dentify.dentify.databinding.PopupPdfBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class AttachmentsFragment : BaseFragment() {
    lateinit var binding: FragmentAttachmentsBinding
    val viewModel: DetailsViewModel by viewModels()
    private lateinit var adapter: AttachmentsRVAdapter
    private lateinit var adapterPatient: AttachmentsRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttachmentsBinding.inflate(layoutInflater)

        setAdapter()
        topBackButton()

        return binding.root
    }

    private fun setAdapter(){
        adapter = AttachmentsRVAdapter(mutableListOf(), object: ViewModelClickListener<Attachment>{
            override fun onClick(item: Attachment) {
                var extension = ""
                if(item.file?.fileName != null){
                    extension = item.file?.fileName!!.substringAfterLast(".")
                }
                if (item.file?.uri != null){
                    if (extension == Constants.JPG || extension == Constants.PNG || extension == Constants.JPEG){
                        showPdfFromUri(item.file?.uri!!, true)
                    }else{
                        showPdfFromUri(item.file?.uri!!, false)
                    }
                }
            }
        }, object: ViewModelClickListener<Attachment>{
            override fun onClick(item: Attachment) {
                downloadPdf(requireContext(), item.file?.uri, item.file?.fileName)
            }
        })
        binding.rvAttachment.adapter = adapter
        adapterPatient = AttachmentsRVAdapter(mutableListOf(), object: ViewModelClickListener<Attachment>{
            override fun onClick(item: Attachment) {
                val extension = item.file?.fileName!!.substringAfterLast(".")
                if (item.file?.uri != null){
                    if (extension == Constants.JPG || extension == Constants.PNG || extension == Constants.JPEG){
                        showPdfFromUri(item.file?.uri!!, true)
                    }else{
                        showPdfFromUri(item.file?.uri!!, false)
                    }
                }
            }
        }, object: ViewModelClickListener<Attachment>{
            override fun onClick(item: Attachment) {
                downloadPdf(requireContext(), item.file?.uri, item.file?.fileName)
            }
        })
        binding.rvAttachmentPatient.adapter = adapterPatient

        updateAttachmentList()
    }

    private fun updateAttachmentList(){
        if (!StorageWrapper.getAppointmentAttachments(requireContext())?.filter { it.isPatientAttachment == false }.isNullOrEmpty()){
            adapter.updateData(StorageWrapper.getAppointmentAttachments(requireContext())!!.filter { it.isPatientAttachment == false })
        }
        if (!StorageWrapper.getAppointmentAttachments(requireContext())?.filter { it.isPatientAttachment == true }.isNullOrEmpty()){
            adapterPatient.updateData(StorageWrapper.getAppointmentAttachments(requireContext())!!.filter { it.isPatientAttachment == true })
        }
    }

    fun downloadPdf(baseActivity: Context, url: String?, title: String?): Long {
        val direct = File(Environment.getExternalStorageDirectory().toString() + "/your_folder")
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val downloadReference: Long
        val  dm: DownloadManager = baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, title
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(title)
        Toast.makeText(baseActivity, getString(R.string.start_downloading), Toast.LENGTH_SHORT).show()
        downloadReference = dm.enqueue(request)

        return downloadReference
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

    private fun topBackButton(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}