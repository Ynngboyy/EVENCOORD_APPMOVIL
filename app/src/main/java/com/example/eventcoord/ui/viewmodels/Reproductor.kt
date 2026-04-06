package com.example.eventcoord.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ReproductorViewModel : ViewModel() {
    // Almacena la URL del video que el usuario seleccionó
    var videoActivoUrl by mutableStateOf<String?>(null)
        private set

    fun seleccionarVideo(url: String) {
        videoActivoUrl = url
    }

    fun cerrarReproductor() {
        videoActivoUrl = null
    }
}