package com.example.gestorpersonaldetareas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gestorpersonaldetareas.model.Tarea

@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class TareaDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var instancia: TareaDatabase? = null

        fun obtenerBaseDatos(context: Context): TareaDatabase {
            return instancia ?: synchronized(this) {
                val nuevaInstancia = Room.databaseBuilder(
                    context.applicationContext,
                    TareaDatabase::class.java,
                    "tareas_database"
                ).build()
                instancia = nuevaInstancia
                nuevaInstancia
            }
        }
    }
}
