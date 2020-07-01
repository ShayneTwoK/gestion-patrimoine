package com.example.toolbartest.ui.inscription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InscriptionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Vous etes sur la page d'inscription"
    }
    val text: LiveData<String> = _text
}