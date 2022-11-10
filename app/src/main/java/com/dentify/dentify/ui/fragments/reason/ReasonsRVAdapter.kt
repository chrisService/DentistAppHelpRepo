package com.dentify.dentify.ui.fragments.reason

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.apiModel.model.MAppointmentReason
import com.dentify.dentify.databinding.VhAppointmentReasonItemBinding
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.util.Constants
import com.dentify.dentify.util.StorageWrapper

class ReasonsRVAdapter(
    val context: Context,
    var reasons: List<MAppointmentReason>,
    val listener: ViewModelClickListener<MAppointmentReason>
): RecyclerView.Adapter<ReasonsRVAdapter.ReasonsViewHolder>() {

    var selectedPosition = StorageWrapper.selectedAppointmentReason + Constants.ADJUST_APPOINTMENT_REASONS_LIST

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_appointment_reason_item, parent, false)
        return ReasonsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReasonsViewHolder, position: Int) {
        val item = reasons[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            listener.onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return reasons.size
    }

    inner class ReasonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = VhAppointmentReasonItemBinding.bind(itemView)
        fun bind(timeSlot: MAppointmentReason){
            with(this){
                binding.textView.text = timeSlot.title
                binding.ivIllustration.setImageDrawable(timeSlot.drawable)

                if(adapterPosition == selectedPosition){
                    binding.root.isSelected = true
                    binding.root.setPadding(20, 0, 0, 0)
                }else{
                    binding.root.isSelected = false
                    binding.root.setPadding(0, 0, 0, 0)
                }
            }
        }
    }
}