package com.example.bruno.model

data class Item( //temos aqui as discricoes de cada item
    val id: Int,
    var name: String,
    var quantity: Int,
    var unit: String,
    var category: Category,
    var isBought: Boolean = false
)
