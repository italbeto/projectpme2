package com.example.prueba2pme.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Producto(
    @PrimaryKey(autoGenerate = true) val id:Int,
    var producto:String,
    var realizada:Boolean
)