package com.dentify.dentify.ui.fragments.appointments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.VhAppointmentItemBinding
import com.dentify.dentify.databinding.VhNoAppoitmentsItemBinding
import com.dentify.dentify.enum.AppointmentStatus
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import java.text.SimpleDateFormat
import java.util.*


class PastAppointmentRVAdapter(
    val context: Context,
    var appointments: MutableList<Appointment>,
    val listener: ViewModelClickListener<Appointment>,
    val status: Int,
    val layoutInflater: LayoutInflater
): RecyclerView.Adapter<PastAppointmentRVAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {

        val view: View

        if (appointments.isEmpty()){
            val emptyStatesBinding = VhNoAppoitmentsItemBinding.inflate(layoutInflater)
            view = emptyStatesBinding.root
            if (status == Constants.PAST){
                view.backgroundTintList = ContextCompat.getColorStateList(context, R.color.emptyPastAppointments)
                emptyStatesBinding.tvNoAppointmentsText.text = context.getString(R.string.you_have_no_past_nappointments)
            }else if (status == Constants.CANCELED){
                view.backgroundTintList = ContextCompat.getColorStateList(context, R.color.emptyCanceledAppointments)
                emptyStatesBinding.tvNoAppointmentsText.text = context.getString(R.string.you_have_no_canceled_nappointments)
            }
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.vh_appointment_item, parent, false)
        }

        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val densityConverter = context.resources.displayMetrics.density
        val horizontalMarginInt = 16*densityConverter.toInt()
        val verticalMarginInt = 8*densityConverter.toInt()
        params.setMargins(horizontalMarginInt, verticalMarginInt, horizontalMarginInt, verticalMarginInt)

        view.layoutParams = params
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        if(!appointments.isEmpty()){
            val item = appointments[position]

            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        if (appointments.isEmpty()){
            return 1
        }else{
            return appointments.size
        }
    }

    fun updateData(data: List<Appointment>){
        appointments.clear()
        appointments.addAll(data)
        notifyDataSetChanged()
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(appointment: Appointment){
            with(this){
                val binding = VhAppointmentItemBinding.bind(itemView)
                if(appointment.status == AppointmentStatus.Done.valueString){
                    binding.root.background = ContextCompat.getDrawable(context, R.drawable.bg_done_appointment_item)
                }else if(appointment.status == AppointmentStatus.Canceled.valueString){
                    binding.root.background = ContextCompat.getDrawable(context, R.drawable.bg_canceled_appointment_item)
                }else{
                    binding.root.background = ContextCompat.getDrawable(context, R.drawable.bg_past_appointment_item)
                }
                val dfAppointment: SimpleDateFormat
                if(StorageWrapper.selectedLocale.isNullOrEmpty()){
                    dfAppointment = SimpleDateFormat("EEE, dd.MM")
                }else{
                    dfAppointment = SimpleDateFormat("EEE, dd.MM", Locale(StorageWrapper.selectedLocale!!))
                }
                val timeZoneParser = Util.dfParser
                timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
                var dateFrom = Date()
                if (!appointment.dateFrom.isNullOrEmpty()){
                    dateFrom = timeZoneParser.parse(appointment.dateFrom!!)!!
                }
                var dateTo = Date()
                if (!appointment.dateTo.isNullOrEmpty()){
                    dateTo = timeZoneParser.parse(appointment.dateTo!!)!!
                }
                val hourFrom = Util.dfClock.format(dateFrom)
                val hourTo = Util.dfClock.format(dateTo)

                binding.tvDoctorName.text = appointment.clinicianName
                binding.tvClinic.text = appointment.clinicName
                binding.tvDate.text = dfAppointment.format(dateFrom)
                binding.tvHour.text = hourFrom+" - "+hourTo
                binding.tvInitials.text = Util.initials(appointment.clinicianName)
                if (!appointment.clinicianProfileImageUri.isNullOrEmpty()){
                    binding.ivClinitianImage.loadCircularImage(appointment.clinicianProfileImageUri, 7F, ContextCompat.getColor(context, R.color.white))
                    binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                }

                binding.root.setOnClickListener {
                    listener.onClick(appointment)
                }
            }
        }
    }
}


