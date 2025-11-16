package com.example.bruno.ui.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bruno.data.repository.ShoppingRepository
import com.example.bruno.model.Category
import com.example.bruno.model.Item
import com.example.bruno.model.ListItem
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingRepository) : ViewModel() {

    private val _listId = MutableLiveData<String>()
    val listId: String get() = _listId.value ?: ""

    private val _listTitle = MutableLiveData<String>("Lista de Compras")
    val listTitle: LiveData<String> get() = _listTitle

    private val _items = MediatorLiveData<List<ListItem>>()
    val items: LiveData<List<ListItem>> get() = _items

    init {
        _items.addSource(repository.items) { items ->
            _items.value = groupAndSort(items)
        }
    }

    private fun groupAndSort(items: List<Item>): List<ListItem> {
        val sorted = items.sortedWith(
            compareBy<Item> { it.bought }
                .thenBy { Category.fromString(it.category).displayName }
                .thenBy { it.name.lowercase() }
        )

        val groupedList = mutableListOf<ListItem>()
        var currentCategory = ""

        sorted.forEach { item ->
            val categoryDisplayName = Category.fromString(item.category).displayName
            if (categoryDisplayName != currentCategory) {
                groupedList.add(ListItem.HeaderItem(categoryDisplayName))
                currentCategory = categoryDisplayName
            }
            groupedList.add(ListItem.ShoppingItem(item))
        }

        return groupedList
    }


    fun setListId(listId: String) {
        _listId.value = listId
        repository.startObservingItems(listId)
        val list = repository.lists.value?.firstOrNull { it.id == listId }
        _listTitle.value = list?.title ?: "Lista de Compras"
    }



    fun setItemQuery(query: String) {
        repository.setItemQuery(query)
    }


    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(listId, item)
        }
    }



    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(listId, item)
        }
    }




    fun toggleBought(item: Item) {
        viewModelScope.launch {
            repository.toggleBought(listId, item)
        }
    }


    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(listId, item.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopObservingItems()
    }
}