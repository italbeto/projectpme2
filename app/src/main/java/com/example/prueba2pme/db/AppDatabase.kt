package com.example.prueba2pme.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Define la base de datos Room que contiene la tabla Producto
@Database(entities = [Producto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao // Proporciona acceso al DAO de Producto

    companion object {
        @Volatile
        private var BASE_DATOS: AppDatabase? = null

        // Devuelve una instancia única de la base de datos usando el patrón Singleton
        fun getInstance(contexto: Context): AppDatabase {
            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "ProductosBD.bd" // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration() // Migración destructiva en caso de cambios en el esquema
                    .build()
                    .also { BASE_DATOS = it } // Almacena la instancia de la base de datos para reutilización
            }
        }
    }
}
