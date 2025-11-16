package com.example.bruno.model

import com.google.firebase.firestore.Exclude

data class Item(
    @get:Exclude var id: String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val quantity: Int = 1,
    val unit: String = "unidade",
    val bought: Boolean = false,
    val category: String = Category.OUTROS.name
)