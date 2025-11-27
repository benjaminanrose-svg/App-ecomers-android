package com.example.teacherstore.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teacherstore.model.Users
import com.example.teacherstore.model.ProductEntity
import com.example.teacherstore.model.CartEntity

@Database(
    entities = [
        Users::class,
        ProductEntity::class,
        CartEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class TeacherAppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
