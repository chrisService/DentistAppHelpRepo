package com.dentify.dentify.ui.fragments.pickDateAndTime

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelApiListener
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.FreeTime
import com.dentify.dentify.apiModel.query.GetClinitianFreeTimesQuery
import com.dentify.dentify.apiModel.response.GetPatientsProfileResponse
import com.dentify.dentify.databinding.FragmentPickDateAndTimeBinding
import com.dentify.dentify.ui.fragments.BaseFragment
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PickDateAndTimeFragment : BaseFragment() {
    lateinit var binding: FragmentPickDateAndTimeBinding
    private lateinit var adapter: TimeSlotsRVAdapter
    lateinit var recyclerView: RecyclerView
    private lateinit var viewAllTextView: TextView
    private lateinit var timeSlotsLayout: ConstraintLayout
    private lateinit var guide1: Guideline
    private lateinit var guide2: Guideline
    private var allShown = false
    private var densityConverter: Float = 0.0F
    private val viewModel: DateAndTimeViewModel by viewModels()
    val timeSlots = mutableListOf<FreeTime>()
    lateinit var dfDateList: SimpleDateFormat
    lateinit var profile: GetPatientsProfileResponse


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickDateAndTimeBinding.inflate(layoutInflater)
        initializeDateFormater()
        topBackButtom()
        initializeView()
        initializeDateFreeTimes()
        showLanguageSpinner(false)
        viewAll()

        return binding.root
    }

    private fun initializeDateFormater() {
        if (StorageWrapper.selectedLocale.isNullOrEmpty()) {
            dfDateList = SimpleDateFormat("EEE, MMM dd")
        } else {
            dfDateList = SimpleDateFormat("EEE, MMM dd", Locale(StorageWrapper.selectedLocale!!))
        }
    }

    private fun initializeDateFreeTimes() {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        if (StorageWrapper.getNDate(requireContext()) != null){
            binding.tvDateText.text = dfDateList.format(StorageWrapper.getNDate(requireContext()))
            getFreeTimes(StorageWrapper.getNDate(requireContext())!!)
        }else{
            binding.tvDateText.text = dfDateList.format(today)
            StorageWrapper.saveNDate(today, requireContext())
            getFreeTimes(today)
        }
        binding.spinnerDate.setOnClickListener {
            openDatePicker()
        }
    }

    private fun initializeView() {

        densityConverter = requireContext().resources.displayMetrics.density
        recyclerView = binding.rvTimeSlots
        viewAllTextView = binding.tvViewAll
        timeSlotsLayout = binding.clTimeSlots
        guide1 = binding.guideline5
        guide2 = binding.guideline8
        if (StorageWrapper.getPatientsProfileResponse(requireContext()) != null){
            profile = StorageWrapper.getPatientsProfileResponse(requireContext())!!
            binding.tvDoctorName.text = profile.clinician?.fullName
            binding.tvInitials.text = Util.initials(profile.clinician?.fullName)
            binding.tvClinic.text = profile.clinic?.clinicName
            if (profile.clinic?.location != null) {
                val address = profile.clinic?.location!!.address
                val city = profile.clinic?.location!!.city
                binding.tvClinicLocation.text = "$address, $city"
            }
            if (!profile.clinician?.profileImageUri.isNullOrEmpty()) {
                binding.ivClinitianImage.loadCircularImage(profile.clinician?.profileImageUri, 7F, ContextCompat.getColor(requireContext(), R.color.white))
                binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            }
            if (!profile.clinic?.logoUri.isNullOrEmpty()) {
                binding.ivClinicImage.loadCircularImage(profile.clinic?.logoUri, 12F, ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        setAdapter()
    }

    private fun setAdapter() {
        adapter = TimeSlotsRVAdapter(requireContext(), mutableListOf(), object :
            ViewModelClickListener<FreeTime> {
            override fun onClick(item: FreeTime) {
                StorageWrapper.saveTimeSlot(item, requireContext())
                findNavController().navigate(R.id.reasonForAppointmentFragment)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        val dateList = ArrayList<String>()
        dateList.add(dfDateList.format(today))
        dateList.add(dfDateList.format(tomorrow))
        val clinicList = ArrayList<String>()
        if (profile.clinic?.clinicName != null){
            clinicList.add(profile.clinic?.clinicName!!)
        }
        val spinnerAdapterClinics: ArrayAdapter<String> =
            object : ArrayAdapter<String>(requireContext(), R.layout.vh_spinner_text, clinicList) {}
        binding.spinnerClinic.adapter = spinnerAdapterClinics
    }

    private fun viewAll() {

        viewAllTextView.setOnClickListener {
            if (!allShown) {
                if (timeSlots.size > 2) {
                    seeAll()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.all_free_slots_are_shown),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                hideAll()
            }
        }
    }

    private fun seeAll() {
        allShown = true
        timeSlotsLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val vaParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        vaParams.topToBottom = recyclerView.id
        vaParams.bottomToBottom = timeSlotsLayout.id
        vaParams.endToStart = guide2.id
        vaParams.startToEnd = guide1.id
        vaParams.setMargins(0, 20 * densityConverter.toInt(), 0, 0)
        viewAllTextView.layoutParams = vaParams
        viewAllTextView.requestLayout()
        viewAllTextView.text = requireContext().resources.getString(R.string.view_less)
        viewAllTextView.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_chevron_up_xs), null
        )

        val rvParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        rvParams.startToEnd = guide1.id
        rvParams.topToBottom = binding.relativeLayout.id
        rvParams.endToStart = guide2.id
        rvParams.bottomToTop = viewAllTextView.id
        recyclerView.layoutParams = rvParams
        recyclerView.requestLayout()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter.updateData(timeSlots)
    }

    fun hideAll() {
        allShown = false
        timeSlotsLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val vaParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        vaParams.topToTop = recyclerView.id
        vaParams.bottomToBottom = recyclerView.id
        vaParams.endToStart = guide2.id
        vaParams.startToEnd = recyclerView.id
        vaParams.setMargins(0, 0, 0, 0)
        viewAllTextView.layoutParams = vaParams
        viewAllTextView.requestLayout()
        viewAllTextView.text = requireContext().resources.getString(R.string.view_all)
        viewAllTextView.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_chevron_down_xs), null
        )

        val rvParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        rvParams.startToEnd = guide1.id
        rvParams.topToBottom = binding.relativeLayout.id
        rvParams.endToStart = viewAllTextView.id
        rvParams.bottomToBottom = timeSlotsLayout.id
        recyclerView.layoutParams = rvParams
        recyclerView.requestLayout()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.updateData(timeSlots.take(2))
    }

    private fun topBackButtom() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun getFreeTimes(nDate: Date) {
        val timeZoneParser = Util.dfParserIsoFormat
        timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
        val calendar = Calendar.getInstance()
        calendar.time = nDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val profileResponse = StorageWrapper.getPatientsProfileResponse(requireContext())
        val dateString = timeZoneParser.format(calendar.time)
        val query = GetClinitianFreeTimesQuery()
        query.clinicianId = profileResponse?.clinician?.id!!
        query.clinicId = profileResponse.clinic?.id!!
        query.date = dateString
        viewModel.getFreeTimes(query, object : ViewModelApiListener {
            override fun onStarted(message: String?) {
                binding.progressBar.isVisible = true
            }

            override fun onSuccess(message: String?) {
                binding.progressBar.isVisible = false
                timeSlots.clear()
                adapter.updateData(timeSlots)
                if (!viewModel.freeTimesResponse.freeTimes.isNullOrEmpty()) {
                    timeSlots.addAll(viewModel.freeTimesResponse.freeTimes!!.filter {
                        val compareDate = timeZoneParser.parse(it.dateFrom)
                        compareDate.after(Calendar.getInstance().time)
                    })
                    hideAll()
                }
            }

            override fun onFailure(message: String?) {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    var uYear   = 0
    var uMonth  = 0
    var uDay    = 0

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val pYear = calendar.get(Calendar.YEAR)
        val pMonth = calendar.get(Calendar.MONTH)
        val pDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            R.style.DatePicker,
            object: android.app.DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    val formater = DecimalFormat("00")
                    val dMonth = formater.format(month + 1).toString()
                    val dDay = formater.format(dayOfMonth).toString()
                    val pickedDateString = "${year}-${dMonth}-${dDay}"
                    val dateUtilHoursString = "T00:00:00"
                    val dateUtilString = pickedDateString + dateUtilHoursString
                    val timeZoneParser = Util.dfParser
                    timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
                    val mDate = timeZoneParser.parse(dateUtilString)
                    StorageWrapper.saveNDate(mDate, requireContext())
                    getFreeTimes(mDate)
                    binding.tvDateText.text = dfDateList.format(mDate)
                    uYear = year
                    uMonth = month
                    uDay = dayOfMonth
                }
            }, pYear, pMonth, pDay
        )
        datePickerDialog.updateDate(uYear, uMonth, uDay)
        datePickerDialog.datePicker.firstDayOfWeek = Calendar.MONDAY
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }
}