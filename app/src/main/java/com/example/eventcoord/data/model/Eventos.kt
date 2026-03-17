package com.example.eventcoord.data.model

data class Evento(
    val id: String = "",
    val titulo: String = "",
    val anfitrion: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val lugar: String = "",
    val creadorId: String = "",
    val programa: List<Actividad> = emptyList(),
    val portada: String = ""
)

data class Actividad(
    val hora: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val esImportante: Boolean = false
)