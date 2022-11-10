package com.dentify.dentify.ui.fragments.pickDateAndTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.apiModel.model.FreeTime
import com.dentify.dentify.databinding.VhTimeSlotBinding
import com.dentify.dentify.util.Util
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.util.Constants
import java.util.*


class TimeSlotsRVAdapter(
    val context: Context,
    var timeSlots: MutableList<FreeTime>,
    val listener: ViewModelClickListener<FreeTime>
): RecyclerView.Adapter<TimeSlotsRVAdapter.TimeSlotsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_time_slot, parent, false)
        return TimeSlotsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotsViewHolder, position: Int) {
        val item = timeSlots[position]

        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }

    fun updateData(data: List<FreeTime>){
        timeSlots.clear()
        timeSlots.addAll(data)
        notifyDataSetChanged()
    }

    inner class TimeSlotsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(timeSlot: FreeTime){
            with(this){
                val binding = VhTimeSlotBinding.bind(itemView)
                val timeZoneParser = Util.dfParser
                timeZoneParser.timeZone = TimeZone.getTimeZone(Constants.GMT)
                if (!timeSlot.dateFrom.isNullOrEmpty()){
                    val slotDate = timeZoneParser.parse(timeSlot.dateFrom!!)
                    binding.root.text = Util.dfClock.format(slotDate!!)
                }
                binding.root.setOnClickListener {
                    listener.onClick(timeSlot)
                }
            }
        }
    }

}



