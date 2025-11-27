package com.example.teacherstore.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class Users(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "correo") val correo: String,
    @ColumnInfo(name = "contrasena") val contrasena: String,
    @ColumnInfo(name = "direccion") val direccion: String,
    @ColumnInfo(name = "telefono") val telefono: String,
    @ColumnInfo(name = "photoUri") val photoUri: String? = null
)
