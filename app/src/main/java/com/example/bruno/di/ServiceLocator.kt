package com.example.bruno.di

import android.content.Context
import com.example.bruno.data.repository.ShoppingRepository

object ServiceLocator {

    @Volatile
    private var repository: ShoppingRepository? = null

    fun provideRepository(context: Context): ShoppingRepository {
        return repository ?: synchronized(this) {
            val newRepo = ShoppingRepository()
            repository = newRepo
            newRepo
        }
    }
}
