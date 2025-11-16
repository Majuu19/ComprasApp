package com.example.bruno.model

import androidx.annotation.DrawableRes
import com.example.bruno.R

enum class Category(val displayName: String) {

    FRUTAS("Frutas"),
    CARNES("Carnes"),
    BEBIDAS("Bebidas"),
    HIGIENE("Higiene"),
    LIMPEZA("Limpeza"),
    PADARIA("Padaria"),
    ALIMENTOS("Alimentos"),
    OUTROS("Outros");

    @DrawableRes
    fun getIcon(): Int {
        return when (this) {
            FRUTAS -> R.drawable.ic_frutas
            CARNES -> R.drawable.ic_carnes
            BEBIDAS -> R.drawable.ic_bebidas
            HIGIENE -> R.drawable.ic_higiene
            LIMPEZA -> R.drawable.ic_limpeza
            PADARIA -> R.drawable.ic_padaria
            ALIMENTOS -> R.drawable.ic_alimentos
            OUTROS -> R.drawable.ic_outros
        }
    }

    companion object {


        fun fromString(value: String): Category {
            return values().find { it.name == value } ?: OUTROS
        }
    }
}
