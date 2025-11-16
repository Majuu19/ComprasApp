package com.example.bruno.ui.shoppinglist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bruno.data.repository.ShoppingRepository
import com.example.bruno.di.ServiceLocator

class ShoppingListVMFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {

            val repository: ShoppingRepository =
                ServiceLocator.provideRepository(context)


            @Suppress("UNCHECKED_CAST")
            return ShoppingListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
