package com.dentify.dentify.ui.fragments.videoCallReview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.apiModel.request.PostVideoReviewRequest
import com.dentify.dentify.databinding.FragmentVideoCallReviewBinding
import com.dentify.dentify.util.StorageWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoCallReviewFragment : Fragment() {

    lateinit var bindind: FragmentVideoCallReviewBinding
    private val viewModel: VideoReviewViewModel by viewModels()
    lateinit var poorIcon: ImageView
    lateinit var fairIcon: ImageView
    lateinit var goodIcon: ImageView
    lateinit var veryGoodIcon: ImageView
    lateinit var excelentIcon: ImageView
    var selectedRating = 0
    var isRatingSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindind = FragmentVideoCallReviewBinding.inflate(layoutInflater)

        poorIcon = bindind.ivPoor
        fairIcon = bindind.ivFair
        goodIcon = bindind.ivGood
        veryGoodIcon = bindind.ivVeryGood
        excelentIcon = bindind.ivExcelent
        addComment()
        ratingIconsClick()
        btnClick()
        return bindind.root
    }

    private fun addComment(){
        bindind.tvAddComment.setOnClickListener {
            bindind.tvAddComment.visibility = View.GONE
            bindind.etRateComment.visibility = View.VISIBLE
        }
    }


    private fun ratingIconsClick(){

        poorIcon.setOnClickListener {
            unclickIcons()
            removeTextUnderIcons()
            poorIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_poor)
            poorIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_poor__active))
            bindind.tvRateText.text = "1/5 Poor"
            selectedRating = 1
            isRatingSelected = true
        }
        fairIcon.setOnClickListener {
            unclickIcons()
            removeTextUnderIcons()
            fairIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_poor)
            fairIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_fair__active))
            bindind.tvRateText.text = "2/5 Fair"
            selectedRating = 2
            isRatingSelected = true
        }
        goodIcon.setOnClickListener {
            unclickIcons()
            removeTextUnderIcons()
            goodIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_good)
            goodIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_good__active))
            bindind.tvRateText.text = "3/5 Good"
            selectedRating = 3
            isRatingSelected = true
        }
        veryGoodIcon.setOnClickListener {
            unclickIcons()
            removeTextUnderIcons()
            veryGoodIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_excelent)
            veryGoodIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_very_good__active))
            bindind.tvRateText.text = "4/5 Very Good"
            selectedRating = 4
            isRatingSelected = true
        }
        excelentIcon.setOnClickListener {
            unclickIcons()
            removeTextUnderIcons()
            excelentIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_excelent)
            excelentIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_exceptional__active))
            bindind.tvRateText.text = "5/5 Exceptional"
            selectedRating = 5
            isRatingSelected = true
        }
    }

    private fun unclickIcons(){
        poorIcon.background = null
        poorIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_poor_50_transparent))
        fairIcon.background = null
        fairIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_fair_50_transparent))
        goodIcon.background = null
        goodIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_good_50_transparent))
        veryGoodIcon.background = null
        veryGoodIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_very_good_50_transparent))
        excelentIcon.background = null
        excelentIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_exceptional_50_transparent))
    }

    private fun removeTextUnderIcons(){
        bindind.tvPoor.visibility = View.GONE
        bindind.tvFair.visibility = View.GONE
        bindind.tvGood.visibility = View.GONE
        bindind.tvVeryGood.visibility = View.GONE
        bindind.tvExceptional.visibility = View.GONE
    }

    private fun btnClick(){
        bindind.btnConfirmRating.setOnClickListener {
            if (isRatingSelected){
                postVideoReview()
            }else{
                Toast.makeText(requireContext(), "Please rate your video call experiance", Toast.LENGTH_SHORT).show()
            }
        }
        bindind.btnSkip.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            findNavController().navigate(R.id.appointmentsFragment, null, navOptions)
        }
    }

    private fun postVideoReview(){
        val body = PostVideoReviewRequest()
        if (!bindind.etMessage.text.isNullOrEmpty()){
            body.feedback = bindind.etMessage.text.toString()
        }
        body.rating = selectedRating

        viewModel.postVideoReview(StorageWrapper.appointmentId!!, body, object: ViewModelApiListener{
            override fun onStarted(message: String?) {
            }
            override fun onSuccess(message: String?) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .build()
                findNavController().navigate(R.id.appointmentsFragment, null, navOptions)
            }
            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}