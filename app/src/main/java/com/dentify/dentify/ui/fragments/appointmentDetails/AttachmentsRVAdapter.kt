package com.dentify.dentify.ui.fragments.appointmentDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.Attachment
import com.dentify.dentify.databinding.VhAttachmentItemBinding

class AttachmentsRVAdapter(
    var attachments: MutableList<Attachment>,
    val openListener: ViewModelClickListener<Attachment>,
    val downloadListener: ViewModelClickListener<Attachment>
): RecyclerView.Adapter<AttachmentsRVAdapter.AttachmentsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_attachment_item, parent, false)
        return AttachmentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentsViewHolder, position: Int) {
        val item = attachments[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    fun updateData(data: List<Attachment>){
        attachments.clear()
        attachments.addAll(data)
        notifyDataSetChanged()
    }

    inner class AttachmentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(attachment: Attachment){
            with(this){
                val binding = VhAttachmentItemBinding.bind(itemView)
                binding.tvFileType.text = attachment.file?.fileName!!.substringAfterLast(".").uppercase()
                binding.tvFileName.text = attachment.file?.fileName!!
                binding.tvOpen.setOnClickListener {
                    openListener.onClick(attachment)
                }
                binding.tvDownload.setOnClickListener {
                    downloadListener.onClick(attachment)
                }
            }
        }
    }
}