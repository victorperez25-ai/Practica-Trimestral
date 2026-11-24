package com.example.gestorpersonaldetareas.network

data class TareaRemota(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
