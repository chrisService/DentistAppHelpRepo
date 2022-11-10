package com.dentify.dentify.ui.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.*
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Appointment
import com.dentify.dentify.databinding.VhAppointmentItemBinding
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper
import com.dentify.dentify.util.Util
import com.dentify.dentify.util.loadCircularImage
import java.text.SimpleDateFormat
import java.util.*


class AppointmentVPAdapter(
    val context: Context,
    var appointments: MutableList<Appointment>,
    val listener: ViewModelClickListener<Appointment>
): RecyclerView.Adapter<AppointmentVPAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view: View

        if (appointments.isEmpty()){
            view = LayoutInflater.from(parent.context).inflate(R.layout.vh_no_appoitments_item, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.vh_appointment_item, parent, false)
        }

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
                val dfAppointment: SimpleDateFormat
                if (StorageWrapper.selectedLocale.isNullOrEmpty()){
                    dfAppointment = SimpleDateFormat("EEE, dd.MM")
                }else{
                    dfAppointment = SimpleDateFormat("EEE, dd.MM", Locale(StorageWrapper.selectedLocale!!))
                }
                val timeZoneParser = Util.dfParser
                timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
                var hourFrom = ""
                var hourTo = ""
                if (!appointment.dateFrom.isNullOrEmpty()){
                    val dateFrom = timeZoneParser.parse(appointment.dateFrom!!)
                    binding.tvDate.text = dfAppointment.format(dateFrom!!)
                    hourFrom = Util.dfClock.format(dateFrom)
                }
                if (!appointment.dateTo.isNullOrEmpty()){
                    val dateTo = timeZoneParser.parse(appointment.dateTo!!)
                    hourTo = Util.dfClock.format(dateTo!!)
                }
                binding.tvDoctorName.text = appointment.clinicianName
                binding.tvClinic.text = appointment.clinicName
                binding.tvHour.text = "$hourFrom - $hourTo"
                binding.tvInitials.text = Util.initials(appointment.clinicianName)

                if (!appointment.clinicianProfileImageUri.isNullOrEmpty()){
                    binding.ivClinitianImage.loadCircularImage(appointment.clinicianProfileImageUri, 7F, ContextCompat.getColor(context, R.color.white))
                    binding.relativeLayout.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                }
                if (!appointments.isEmpty()){
                    binding.root.setOnClickListener {
                        listener.onClick(appointment)
                    }
                }
            }
        }
    }
}


