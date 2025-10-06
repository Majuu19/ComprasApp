package com.example.bruno.ui.shoppinglist
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bruno.data.FakeDatabase
import com.example.bruno.model.Item
import com.example.bruno.model.ShoppingList

class ShoppingListViewModel : ViewModel() {
    private val _filteredItems = MutableLiveData<List<Item>>()
    private val _list = MutableLiveData<ShoppingList?>()
    val list: LiveData<ShoppingList?> = _list

    private val _items = MutableLiveData<List<Item>>()
    private val _searchQuery = MutableLiveData("")

    val filteredItems: LiveData<List<Item>> = MediatorLiveData<List<Item>>().apply {
        fun update() {
            val items = _items.value ?: return
            val query = _searchQuery.value?.lowercase() ?: ""
            val filtered = if (query.isBlank()) {
                items
            } else {
                items.filter {
                    it.name.lowercase().contains(query) ||
                    it.category.displayName.lowercase().contains(query)
                }
            }
            value = filtered
        }

        addSource(_items) { update() }
        addSource(_searchQuery) { update() }
    }

    fun loadList(listId: Int) {
        val shoppingList = FakeDatabase.shoppingLists.firstOrNull { it.id == listId }
        _list.value = shoppingList
        _items.value = shoppingList?.items ?: emptyList()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addItem(item: Item) {
        _list.value?.items?.add(item)
        refreshItems()
    }

    fun updateItem(item: Item) {
        val currentList = _list.value

        if (currentList != null) {
            val index = currentList.items.indexOfFirst { it.id == item.id }

            if (index >= 0) {

                currentList.items[index] = item


                _list.value = currentList
                _filteredItems.value = currentList.items.toList()
            }
        }
    }
    fun deleteItem(item: Item) {
        _list.value?.items?.remove(item)
        refreshItems()
    }

    fun toggleBought(item: Item) {
        item.isBought = !item.isBought
        refreshItems()
    }

    private fun refreshItems() {
        _items.value = _list.value?.items?.toList() ?: emptyList()
    }
}
