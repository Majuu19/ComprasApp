package com.example.bruno.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bruno.data.repository.ShoppingRepository
import com.example.bruno.model.ShoppingList
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val lists: LiveData<List<ShoppingList>> get() = repository.lists

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val editingList = MutableLiveData<ShoppingList?>()
    private val _pendingImageUri = MutableLiveData<Uri?>()
    val pendingTitle = MutableLiveData<String?>()

    fun start() = repository.startObservingLists()

    fun stop() = repository.clearListsListener()

    fun setQuery(query: String) {
        repository.setListQuery(query)
    }

    fun setEditingList(list: ShoppingList?) {
        editingList.value = list
    }

    fun setPendingImageUri(uri: Uri?) {
        _pendingImageUri.value = uri
    }

    fun setPendingTitle(title: String?) {
        pendingTitle.value = title
    }

    fun saveList(title: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imageUri = _pendingImageUri.value
                val currentList = editingList.value
                if (currentList != null) {
                    repository.updateList(currentList, title, imageUri)
                } else {
                    repository.addList(title, imageUri)
                }
                _pendingImageUri.value = null
                editingList.value = null
                pendingTitle.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteList(list: ShoppingList) {
        viewModelScope.launch {
            repository.deleteList(list)
        }
    }
}
