package com.example.gestorpersonaldetareas.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.gestorpersonaldetareas.model.Tarea

fun compartirTarea(context: Context, tarea: Tarea) {
    val descripcion = tarea.descripcion.ifBlank { "(sin descripción)" }
    val texto = "Tarea: ${tarea.titulo}\n" +
            "Descripción: $descripcion\n" +
            "Prioridad: ${tarea.prioridad.etiqueta}"

    val intentEnviar = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, tarea.titulo)
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    val selector = Intent.createChooser(intentEnviar, "Compartir tarea con...")

    try {
        context.startActivity(selector)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No hay ninguna aplicación instalada que permita compartir",
            Toast.LENGTH_LONG
        ).show()
    }
}
