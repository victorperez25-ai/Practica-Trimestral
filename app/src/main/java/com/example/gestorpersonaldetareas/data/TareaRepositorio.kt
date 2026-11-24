package com.example.gestorpersonaldetareas.data

import com.example.gestorpersonaldetareas.model.Tarea
import kotlinx.coroutines.flow.Flow

class TareaRepositorio(private val tareaDao: TareaDao) {

    val todasLasTareas: Flow<List<Tarea>> = tareaDao.obtenerTodas()

    suspend fun obtenerTarea(id: Int): Tarea? = tareaDao.obtenerPorId(id)

    suspend fun insertarTarea(tarea: Tarea) = tareaDao.insertar(tarea)

    suspend fun actualizarTarea(tarea: Tarea) = tareaDao.actualizar(tarea)

    suspend fun eliminarTarea(tarea: Tarea) = tareaDao.eliminar(tarea)

    suspend fun cambiarCompletada(tarea: Tarea, completada: Boolean) =
        tareaDao.actualizar(tarea.copy(completada = completada))
}
