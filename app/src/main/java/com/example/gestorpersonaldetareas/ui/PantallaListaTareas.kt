package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.R
import com.example.gestorpersonaldetareas.data.TareaRepositorio
import com.example.gestorpersonaldetareas.model.Prioridad
import com.example.gestorpersonaldetareas.model.Tarea
import com.example.gestorpersonaldetareas.preferences.PreferenciasRepositorio
import com.example.gestorpersonaldetareas.util.compartirTarea
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaTareas(
    tareaRepositorio: TareaRepositorio,
    preferenciasRepositorio: PreferenciasRepositorio,
    alCrearTarea: () -> Unit,
    alEditarTarea: (Tarea) -> Unit,
    alAbrirTareasEjemplo: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val tareas by tareaRepositorio.todasLasTareas.collectAsState(initial = null)
    val soloPendientes by preferenciasRepositorio.mostrarSoloPendientes.collectAsState(initial = false)

    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tarea),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("Mis tareas")
                    }
                },
                actions = {
                    TextButton(onClick = alAbrirTareasEjemplo) {
                        Icon(painterResource(R.drawable.ic_nube), contentDescription = null)
                        Spacer(Modifier.size(4.dp))
                        Text("Tareas de ejemplo")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alCrearTarea,
                icon = { Icon(painterResource(R.drawable.ic_anadir), contentDescription = null) },
                text = { Text("Nueva tarea") }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val pantallaAncha = maxWidth >= 600.dp

            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize()
            ) {
                FilaSoloPendientes(
                    activado = soloPendientes,
                    alCambiar = { nuevoValor ->
                        scope.launch { preferenciasRepositorio.guardarMostrarSoloPendientes(nuevoValor) }
                    }
                )
                HorizontalDivider()

                val listaActual = tareas
                if (listaActual == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val tareasVisibles =
                        if (soloPendientes) listaActual.filter { !it.completada } else listaActual

                    if (tareasVisibles.isEmpty()) {
                        val mensaje = if (listaActual.isEmpty()) {
                            "Todavía no tienes tareas.\nPulsa «Nueva tarea» para crear la primera."
                        } else {
                            "No tienes tareas pendientes.\nDesactiva el filtro para ver las completadas."
                        }
                        MensajeListaVacia(mensaje)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tareasVisibles, key = { it.id }) { tarea ->
                                TarjetaTarea(
                                    tarea = tarea,
                                    mostrarDescripcion = pantallaAncha,
                                    alCambiarCompletada = { completada ->
                                        scope.launch { tareaRepositorio.cambiarCompletada(tarea, completada) }
                                    },
                                    alEditar = { alEditarTarea(tarea) },
                                    alCompartir = { compartirTarea(context, tarea) },
                                    alEliminar = { tareaAEliminar = tarea }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val tareaPendienteDeBorrar = tareaAEliminar
    if (tareaPendienteDeBorrar != null) {
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que quieres eliminar «${tareaPendienteDeBorrar.titulo}»? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { tareaRepositorio.eliminarTarea(tareaPendienteDeBorrar) }
                    tareaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaSoloPendientes(activado: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Mostrar solo pendientes", modifier = Modifier.weight(1f))
        Switch(checked = activado, onCheckedChange = alCambiar)
    }
}

@Composable
private fun MensajeListaVacia(mensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tarea),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(16.dp))
        Text(mensaje, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun TarjetaTarea(
    tarea: Tarea,
    mostrarDescripcion: Boolean,
    alCambiarCompletada: (Boolean) -> Unit,
    alEditar: () -> Unit,
    alCompartir: () -> Unit,
    alEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = alCambiarCompletada)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (mostrarDescripcion && tarea.descripcion.isNotBlank()) {
                    Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = "Prioridad: ${tarea.prioridad.etiqueta}",
                    color = colorPrioridad(tarea.prioridad),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (tarea.completada) "Estado: Completada" else "Estado: Pendiente",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = alEditar) {
                Icon(painterResource(R.drawable.ic_editar), contentDescription = "Editar")
            }
            IconButton(onClick = alCompartir) {
                Icon(painterResource(R.drawable.ic_compartir), contentDescription = "Compartir")
            }
            IconButton(onClick = alEliminar) {
                Icon(
                    painterResource(R.drawable.ic_borrar),
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.ALTA -> Color(0xFFC62828)
    Prioridad.MEDIA -> Color(0xFFEF6C00)
    Prioridad.BAJA -> Color(0xFF2E7D32)
}
