package com.example.gestorpersonaldetareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestorpersonaldetareas.data.TareaDatabase
import com.example.gestorpersonaldetareas.data.TareaRepositorio
import com.example.gestorpersonaldetareas.preferences.PreferenciasRepositorio
import com.example.gestorpersonaldetareas.ui.AppGestorTareas
import com.example.gestorpersonaldetareas.ui.theme.GestorPersonalDeTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDatos = TareaDatabase.obtenerBaseDatos(applicationContext)
        val tareaRepositorio = TareaRepositorio(baseDatos.tareaDao())
        val preferenciasRepositorio = PreferenciasRepositorio(applicationContext)

        setContent {
            GestorPersonalDeTareasTheme {
                AppGestorTareas(
                    tareaRepositorio = tareaRepositorio,
                    preferenciasRepositorio = preferenciasRepositorio
                )
            }
        }
    }
}
