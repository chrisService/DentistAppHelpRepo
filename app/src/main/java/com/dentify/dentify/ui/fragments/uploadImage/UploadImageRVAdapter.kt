package com.dentify.dentify.ui.fragments.uploadImage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dentify.dentify.R
import com.dentify.dentify.`interface`.CollectionChangeListener
import com.dentify.dentify.databinding.VhUploadImageItemBinding
import com.dentify.dentify.`interface`.ViewModelClickListener
import com.dentify.dentify.apiModel.model.MUploadedFile

class UploadImageRVAdapter(
    var files: MutableList<MUploadedFile>,
    private val listener: ViewModelClickListener<MUploadedFile>,
    val cListener: CollectionChangeListener,
    private val deleteListener: ViewModelClickListener<MUploadedFile>,
    val context: Context
): RecyclerView.Adapter<UploadImageRVAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_upload_image_item, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val item = files[position]
        holder.bind(item)

        holder.fileName.text = item.fileName?.substringBeforeLast(".")
        holder.fileExtension.text = ".${item.fileName?.substringAfterLast(".")}"

        holder.preview.setOnClickListener {
            listener.onClick(item)
        }

        holder.delete.setOnClickListener {
            files.remove(item)
            cListener.onCollectionChanged(files.size)
            notifyItemRemoved(position)
            deleteListener.onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    inner class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var delete: ImageView
        lateinit var preview: TextView
        lateinit var fileName: TextView
        lateinit var fileExtension: TextView

        fun bind(image: MUploadedFile){
            with(this){
                val binding = VhUploadImageItemBinding.bind(itemView)
                delete = binding.ivDelete
                preview = binding.tvPreview
                fileName = binding.tvFileName
                fileExtension = binding.tvExtension
            }
        }
    }
}