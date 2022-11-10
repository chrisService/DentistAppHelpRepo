package com.dentify.dentify.ui.fragments.appoitmentRequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentify.dentify.R
import com.dentify.dentify.databinding.FragmentRequestNotesBinding
import com.dentify.dentify.util.Constants


class RequestNotesFragment : Fragment() {

    lateinit var binding: FragmentRequestNotesBinding
    lateinit var adapter: RequestsNotesRVAdapter
    var requestNote: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestNotesBinding.inflate(layoutInflater)

        initializeNote()
        backBtn()

        return binding.root
    }

    private fun initializeNote(){
        if (arguments != null){
            requestNote = requireArguments().getString(Constants.APPOINTMENT_REQUEST_NOTE)
        }
        if (!requestNote.isNullOrEmpty()){
            setAdapter()
        }
    }

    private fun setAdapter(){
        adapter = RequestsNotesRVAdapter(mutableListOf())
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = adapter
        val notes = mutableListOf<String>()
        notes.add(requestNote!!)
        adapter.updateData(notes)
    }

    private fun backBtn(){
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}