package com.example.gestorpersonaldetareas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.gestorpersonaldetareas.data.TareaRepositorio
import com.example.gestorpersonaldetareas.preferences.PreferenciasRepositorio

const val PANTALLA_LISTA = "lista"
const val PANTALLA_FORMULARIO = "formulario"
const val PANTALLA_EJEMPLO = "ejemplo"

@Composable
fun AppGestorTareas(
    tareaRepositorio: TareaRepositorio,
    preferenciasRepositorio: PreferenciasRepositorio
) {
    var pantallaActual by rememberSaveable { mutableStateOf(PANTALLA_LISTA) }
    var idTareaEditando by rememberSaveable { mutableStateOf<Int?>(null) }

    BackHandler(enabled = pantallaActual != PANTALLA_LISTA) {
        pantallaActual = PANTALLA_LISTA
    }

    when (pantallaActual) {
        PANTALLA_FORMULARIO -> PantallaFormularioTarea(
            tareaRepositorio = tareaRepositorio,
            idTarea = idTareaEditando,
            alVolver = { pantallaActual = PANTALLA_LISTA }
        )

        PANTALLA_EJEMPLO -> PantallaTareasEjemplo(
            alVolver = { pantallaActual = PANTALLA_LISTA }
        )

        else -> PantallaListaTareas(
            tareaRepositorio = tareaRepositorio,
            preferenciasRepositorio = preferenciasRepositorio,
            alCrearTarea = {
                idTareaEditando = null
                pantallaActual = PANTALLA_FORMULARIO
            },
            alEditarTarea = { tarea ->
                idTareaEditando = tarea.id
                pantallaActual = PANTALLA_FORMULARIO
            },
            alAbrirTareasEjemplo = { pantallaActual = PANTALLA_EJEMPLO }
        )
    }
}
