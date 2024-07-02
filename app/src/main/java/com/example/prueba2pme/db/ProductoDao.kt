package com.example.prueba2pme.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Define un Data Access Object (DAO) para la entidad Producto
@Dao
interface ProductoDao {

    // Consulta para obtener todos los productos ordenados por el estado de realización
    @Query("SELECT * FROM Producto ORDER BY realizada")
    fun findAll(): List<Producto>

    // Consulta para contar la cantidad de productos en la tabla
    @Query("SELECT COUNT(*) FROM Producto")
    fun contar(): Int

    // Inserta un nuevo producto en la tabla y devuelve el ID generado
    @Insert
    fun insertar(producto: Producto): Long

    // Actualiza un producto existente en la tabla
    @Update
    fun actualizar(producto: Producto)

    // Elimina un producto de la tabla
    @Delete
    fun eliminar(producto: Producto)
}
