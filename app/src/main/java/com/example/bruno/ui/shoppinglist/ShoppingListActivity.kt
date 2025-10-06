package com.example.bruno.ui.shoppinglist
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bruno.data.FakeDatabase
import com.example.bruno.databinding.ActivityShoppingListBinding
import com.example.bruno.model.Category
import com.example.bruno.model.Item
import com.example.bruno.ui.shoppinglist.adapter.ItemListAdapter

class ShoppingListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LIST_ID = "LIST_ID"
    }

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var adapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ShoppingListViewModel::class.java)

        val listId = intent.getIntExtra(EXTRA_LIST_ID, -1)
        viewModel.loadList(listId)

        setupRecyclerView()
        observeViewModel()

        viewModel.list.observe(this) {
            title = it?.title ?: "Lista de Compras"
        }


        binding.fabAddItem.setOnClickListener {
            showItemDialog(null)
        }


        binding.inputSearchItems.addTextChangedListener { text ->
            viewModel.setSearchQuery(text.toString())
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnVoltar.setOnClickListener {
            finish() // Fecha a tela atual e volta para a MainActivity
        }
    }

    private fun setupRecyclerView() {
        adapter = ItemListAdapter(
            onCheck = { item -> viewModel.toggleBought(item) },
            onEdit = { item -> showItemDialog(item) },
            onDelete = { item -> viewModel.deleteItem(item) }
        )
        binding.recyclerItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerItems.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.filteredItems.observe(this) {
            adapter.updateData(it)
        }
    }

    private fun showItemDialog(item: Item?) {
        val isEditing = item != null
        val title = if (isEditing) "Editar Item" else "Adicionar Item"

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


        if (isEditing) {
            inputName.setText(item?.name)
            inputQuantity.setText(item?.quantity.toString())
            val unitIndex = unidades.indexOf(item?.unit)
            if (unitIndex >= 0) spinnerUnit.setSelection(unitIndex)
            spinnerCategory.setSelection(item?.category?.ordinal ?: 0)
        }

        layout.addView(inputName)
        layout.addView(inputQuantity)
        layout.addView(spinnerUnit)
        layout.addView(spinnerCategory)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("Salvar") { d, _ ->
                val name = inputName.text.toString().trim()
                val quantity = inputQuantity.text.toString().toIntOrNull() ?: 1
                val unit = spinnerUnit.selectedItem.toString()
                val category = Category.values()[spinnerCategory.selectedItemPosition]

                if (name.isNotEmpty()) {
                    if (isEditing) {
                        item?.apply {
                            this.name = name
                            this.quantity = quantity
                            this.unit = unit
                            this.category = category
                        }
                        item?.let { viewModel.updateItem(it) }
                    } else {
                        val newItem = Item(
                            id = FakeDatabase.nextItemId(),
                            name = name,
                            quantity = quantity,
                            unit = unit,
                            category = category
                        )
                        viewModel.addItem(newItem)
                    }
                }
                d.dismiss()
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
