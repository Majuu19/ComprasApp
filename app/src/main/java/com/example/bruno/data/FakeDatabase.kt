package com.example.bruno.data
import com.example.bruno.model.ShoppingList
import com.example.bruno.model.User

object FakeDatabase {

    val users = mutableListOf<User>()
    val shoppingLists = mutableListOf<ShoppingList>()
    private var listAutoId = 1
    private var itemAutoId = 1
    fun nextListId(): Int = listAutoId++
    fun nextItemId(): Int = itemAutoId++

    fun clearAll() {
        users.clear()
        shoppingLists.clear()
        listAutoId = 1
        itemAutoId = 1
    }
}
