package com.example.bruno.model

data class ShoppingList( // descriçao de cada lista de compra
    val id: Int,
    var title: String,
    var imageUri: String? = null,
    val items: MutableList<Item> = mutableListOf()
)
