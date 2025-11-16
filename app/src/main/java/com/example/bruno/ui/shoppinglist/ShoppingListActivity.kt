package com.example.bruno.ui.shoppinglist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bruno.databinding.ActivityShoppingListBinding
import com.example.bruno.model.Category
import com.example.bruno.model.Item
import com.example.bruno.ui.shoppinglist.adapter.ItemListAdapter

class ShoppingListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LIST_ID = "LIST_ID"
    }

    private lateinit var binding: ActivityShoppingListBinding

    private val vm: ShoppingListViewModel by viewModels {
        ShoppingListVMFactory(this)
    }

    private lateinit var adapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        setupListeners()
        setupObservers()

        val listId = intent.getStringExtra(EXTRA_LIST_ID) ?: ""
        vm.setListId(listId)
    }

    private fun setupRecycler() {
        adapter = ItemListAdapter(
            onCheck = { item -> vm.toggleBought(item) },
            onEdit = { item -> showItemDialog(item) },
            onDelete = { item -> vm.deleteItem(item) }
        )

        binding.recyclerItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerItems.adapter = adapter
    }

    private fun setupListeners() {

        binding.fabAddItem.setOnClickListener {
            showItemDialog(null)
        }

        binding.inputSearchItems.addTextChangedListener { text ->
            vm.setItemQuery(text.toString())
        }

        binding.btnVoltar.setOnClickListener { finish() }
    }

    private fun setupObservers() {
        vm.listTitle.observe(this) {
            title = it
        }

        vm.items.observe(this) { items ->
            adapter.submitList(items)
        }
    }

    private fun showItemDialog(item: Item?) {

        val editing = item != null
        val dialogTitle = if (editing) "Editar Item" else "Adicionar Item"

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val inputName = EditText(this).apply { hint = "Nome do item" }
        val inputQuantity = EditText(this).apply {
            hint = "Quantidade"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val unidades = listOf("unidade", "kg", "g", "L", "mL", "caixa", "pacote")
        val spinnerUnit = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@ShoppingListActivity,
                android.R.layout.simple_spinner_dropdown_item,
                unidades
            )
        }

        val spinnerCategory = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@ShoppingListActivity,
                android.R.layout.simple_spinner_dropdown_item,
                Category.values().map { it.displayName }
            )
        }

        if (editing) {
            inputName.setText(item!!.name)
            inputQuantity.setText(item.quantity.toString())
            spinnerUnit.setSelection(unidades.indexOf(item.unit))

            val enumCat = Category.fromString(item.category)
            spinnerCategory.setSelection(enumCat.ordinal)
        }

        layout.addView(inputName)
        layout.addView(inputQuantity)
        layout.addView(spinnerUnit)
        layout.addView(spinnerCategory)

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(layout)
            .setPositiveButton("Salvar") { dialog, _ ->

                val name = inputName.text.toString().trim()
                val quantity = inputQuantity.text.toString().toIntOrNull() ?: 1
                val unit = spinnerUnit.selectedItem.toString()
                val categoryEnum = Category.values()[spinnerCategory.selectedItemPosition]
                val categoryString = categoryEnum.name

                if (name.isNotEmpty()) {

                    if (editing) {
                        val updated = item!!.copy(
                            name = name,
                            quantity = quantity,
                            unit = unit,
                            category = categoryString
                        )
                        vm.updateItem(updated)

                    } else {
                        val newItem = Item(
                            name = name,
                            quantity = quantity,
                            unit = unit,
                            category = categoryString,
                        )
                        vm.addItem(newItem)
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}