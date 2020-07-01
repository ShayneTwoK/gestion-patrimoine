package com.example.toolbartest.ui.connexion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConnexionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Vous etes sur la page de connexion"
    }
    val text: LiveData<String> = _text
}