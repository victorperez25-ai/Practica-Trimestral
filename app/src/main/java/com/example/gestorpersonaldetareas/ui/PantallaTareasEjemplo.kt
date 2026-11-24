package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.R
import com.example.gestorpersonaldetareas.network.TareaRemota
import com.example.gestorpersonaldetareas.network.TareasApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface EstadoConsulta {
    object Inicial : EstadoConsulta
    object Cargando : EstadoConsulta
    data class Exito(val tareas: List<TareaRemota>) : EstadoConsulta
    data class Error(val mensaje: String) : EstadoConsulta
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareasEjemplo(alVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    var estado by remember { mutableStateOf<EstadoConsulta>(EstadoConsulta.Inicial) }

    fun cargarTareas() {
        if (estado is EstadoConsulta.Cargando) return
        estado = EstadoConsulta.Cargando
        scope.launch {
            estado = try {
                val tareas = TareasApi.servicio.obtenerTareas(userId = 1)
                EstadoConsulta.Exito(tareas)
            } catch (e: IOException) {
                EstadoConsulta.Error("No se ha podido conectar con el servidor. Comprueba tu conexión a Internet.")
            } catch (e: HttpException) {
                EstadoConsulta.Error("El servidor respondió con un error (código ${e.code()}).")
            } catch (e: Exception) {
                EstadoConsulta.Error("Se produjo un error inesperado: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas de ejemplo") },
                actions = { TextButton(onClick = alVolver) { Text("Volver") } }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Estas tareas se obtienen del servicio web JSONPlaceholder. " +
                            "Son datos de ejemplo y no forman parte de tus tareas personales.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { cargarTareas() },
                    enabled = estado !is EstadoConsulta.Cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_nube), contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Cargar tareas del servidor")
                }
                Spacer(Modifier.height(12.dp))

                when (val estadoActual = estado) {
                    EstadoConsulta.Inicial -> MensajeCentrado("Pulsa el botón para consultar las tareas de ejemplo.")

                    EstadoConsulta.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("Cargando tareas...")
                        }
                    }

                    is EstadoConsulta.Error -> Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            estadoActual.mensaje,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { cargarTareas() }) { Text("Reintentar") }
                    }

                    is EstadoConsulta.Exito -> {
                        if (estadoActual.tareas.isEmpty()) {
                            MensajeCentrado("El servidor no ha devuelto ninguna tarea.")
                        } else {
                            Text(
                                "${estadoActual.tareas.size} tareas recibidas",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(estadoActual.tareas, key = { it.id }) { tarea ->
                                    TarjetaTareaRemota(tarea)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeCentrado(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(texto, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TarjetaTareaRemota(tarea: TareaRemota) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#${tarea.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tarea.title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    if (tarea.completed) "Estado: Completada" else "Estado: Pendiente",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (tarea.completed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
