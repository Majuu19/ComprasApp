package com.example.bruno.model

import com.google.firebase.firestore.Exclude

data class ShoppingList(
    @get:Exclude
    var id: String = "",
    val title: String = "",
    val titleLowercase: String = "",
    val imageUrl: String? = null,
    val userId: String = ""
)
