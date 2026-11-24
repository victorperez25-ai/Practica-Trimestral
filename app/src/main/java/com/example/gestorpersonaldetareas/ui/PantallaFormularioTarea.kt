package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.data.TareaRepositorio
import com.example.gestorpersonaldetareas.model.Prioridad
import com.example.gestorpersonaldetareas.model.Tarea
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioTarea(
    tareaRepositorio: TareaRepositorio,
    idTarea: Int?,
    alVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val esEdicion = idTarea != null

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var prioridadSeleccionada by rememberSaveable { mutableStateOf<Prioridad?>(null) }
    var completada by rememberSaveable { mutableStateOf(false) }
    var datosCargados by rememberSaveable { mutableStateOf(!esEdicion) }

    var errorTitulo by rememberSaveable { mutableStateOf<String?>(null) }
    var errorPrioridad by rememberSaveable { mutableStateOf<String?>(null) }

    var guardando by remember { mutableStateOf(false) }

    LaunchedEffect(idTarea) {
        if (idTarea != null && !datosCargados) {
            val tarea = tareaRepositorio.obtenerTarea(idTarea)
            if (tarea != null) {
                titulo = tarea.titulo
                descripcion = tarea.descripcion
                prioridadSeleccionada = tarea.prioridad
                completada = tarea.completada
            }
            datosCargados = true
        }
    }

    fun validarYGuardar() {
        if (guardando) return

        val tituloLimpio = titulo.trim()
        errorTitulo = when {
            tituloLimpio.isEmpty() -> "El título es obligatorio"
            tituloLimpio.length < 3 -> "El título debe tener al menos 3 caracteres"
            else -> null
        }
        errorPrioridad = if (prioridadSeleccionada == null) "Selecciona una prioridad" else null

        val prioridad = prioridadSeleccionada
        if (errorTitulo != null || prioridad == null) return

        guardando = true
        scope.launch {
            val tarea = Tarea(
                id = idTarea ?: 0,
                titulo = tituloLimpio,
                descripcion = descripcion.trim(),
                prioridad = prioridad,
                completada = completada
            )
            if (esEdicion) {
                tareaRepositorio.actualizarTarea(tarea)
            } else {
                tareaRepositorio.insertarTarea(tarea)
            }
            alVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (esEdicion) "Editar tarea" else "Nueva tarea") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!datosCargados) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = {
                            titulo = it
                            errorTitulo = null
                        },
                        label = { Text("Título *") },
                        isError = errorTitulo != null,
                        supportingText = { errorTitulo?.let { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción (opcional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    SelectorPrioridad(
                        prioridadSeleccionada = prioridadSeleccionada,
                        error = errorPrioridad,
                        alSeleccionar = {
                            prioridadSeleccionada = it
                            errorPrioridad = null
                        }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = alVolver,
                            enabled = !guardando,
                            modifier = Modifier.weight(1f)
                        ) { Text("Cancelar") }

                        Button(
                            onClick = { validarYGuardar() },
                            enabled = !guardando,
                            modifier = Modifier.weight(1f)
                        ) { Text(if (guardando) "Guardando..." else "Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorPrioridad(
    prioridadSeleccionada: Prioridad?,
    error: String?,
    alSeleccionar: (Prioridad) -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = prioridadSeleccionada?.etiqueta ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Prioridad *") },
            placeholder = { Text("Selecciona una prioridad") },
            trailingIcon = { Text(if (menuAbierto) "▲" else "▼") },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { menuAbierto = true }
        )
        DropdownMenu(
            expanded = menuAbierto,
            onDismissRequest = { menuAbierto = false }
        ) {
            Prioridad.entries.forEach { prioridad ->
                DropdownMenuItem(
                    text = { Text(prioridad.etiqueta) },
                    onClick = {
                        alSeleccionar(prioridad)
                        menuAbierto = false
                    }
                )
            }
        }
    }
}
