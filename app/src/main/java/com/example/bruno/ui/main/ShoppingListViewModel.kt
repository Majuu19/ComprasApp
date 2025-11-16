package com.example.bruno.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bruno.data.repository.ShoppingRepository
import com.example.bruno.model.ShoppingList
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {


    val lists: LiveData<List<ShoppingList>> = repository.lists


    fun start() {
        repository.startObservingLists()
    }


    fun stop() {
        repository.clearListsListener()
    }


    fun saveList(title: String, imageUri: Uri?, editingList: ShoppingList?) {
        viewModelScope.launch {
            if (editingList != null) {
                repository.updateList(editingList, title, imageUri)
            } else {
                repository.addList(title, imageUri)
            }
        }
    }


    fun deleteList(list: ShoppingList) {
        viewModelScope.launch {

            repository.deleteList(list)
        }
    }
}
