package com.example.bruno.model


sealed class ListItem {

    data class HeaderItem(val category: String) : ListItem() {

        val id = category
    }


    data class ShoppingItem(val item: Item) : ListItem() {

        val id = item.id
    }
}
