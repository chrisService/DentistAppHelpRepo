package com.dentify.dentify.ui.fragments.appoitmentRequests

import com.dentify.dentify.mainViewModel.MainViewModel
import com.dentify.dentify.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(private val repository: MainRepository) :
    MainViewModel(repository) {


}