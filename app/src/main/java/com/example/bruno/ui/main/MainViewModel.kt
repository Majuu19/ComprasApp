package com.example.bruno.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bruno.model.ShoppingList

class MainViewModel : ViewModel() {

    private val _pendingImageUri = MutableLiveData<Uri?>()
    val pendingImageUri: LiveData<Uri?> = _pendingImageUri

    private val _editingList = MutableLiveData<ShoppingList?>()
    val editingList: LiveData<ShoppingList?> = _editingList

    fun setPendingImageUri(uri: Uri?) {
        _pendingImageUri.value = uri
    }

    fun setEditingList(list: ShoppingList?) {
        _editingList.value = list
        if (list == null) {
            _pendingImageUri.value = null
        }
    }
}