package com.dentify.dentify.ui.fragments.appoitmentRequests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.databinding.VhNotesItemBinding


class RequestsNotesRVAdapter(
    var notes: MutableList<String>
): RecyclerView.Adapter<RequestsNotesRVAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_notes_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = notes[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateData(data: List<String>){
        notes.clear()
        notes.addAll(data)
        notifyDataSetChanged()
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: String){
            with(this){
                val binding = VhNotesItemBinding.bind(itemView)
                binding.root.text = note
            }
        }
    }
}


