package com.example.bruno.ui.main
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bruno.data.FakeDatabase
import com.example.bruno.databinding.ActivityMainBinding
import com.example.bruno.model.ShoppingList
import com.example.bruno.ui.login.LoginActivity
import com.example.bruno.ui.main.adapter.ShoppingListAdapter
import com.example.bruno.ui.shoppinglist.ShoppingListActivity


class MainActivity : AppCompatActivity() {// Tela principal do app
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var mainViewModel: MainViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            mainViewModel.setPendingImageUri(uri)
            showListDialog(mainViewModel.editingList.value)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        adapter = ShoppingListAdapter(
            onClick = { list ->
                // abre a tela de itens da lista
                val it = Intent(this, ShoppingListActivity::class.java)
                it.putExtra(ShoppingListActivity.EXTRA_LIST_ID, list.id) // passar o ID da lista
                startActivity(it)
            },
            onEdit = { list ->
                showListDialog(list)
            },
            onDelete = { list ->
                // remover a lista e todos os seus itens
                list.items.clear()
                FakeDatabase.shoppingLists.removeAll { it.id == list.id }
                refresh() // atualiza a tela
            }
        )


        binding.recyclerShoppingLists.layoutManager = LinearLayoutManager(this)
        binding.recyclerShoppingLists.adapter = adapter

        binding.fabAddList.setOnClickListener {
            showListDialog(null)
        }

        // botao para sair do aplicativo
        binding.btnLogout.setOnClickListener {

            // apaga todos os dados do banco em memória
            FakeDatabase.clearAll()

            // volta para a tela de Login e limpa a memoria activits
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // pesquisa e filtra as listas em tempo real
        binding.inputSearchLists.addTextChangedListener { text ->
            val query = text.toString().lowercase()

            // filtra listas pelo texto digitado
            val filtered = FakeDatabase.shoppingLists.filter {
                it.title.lowercase().contains(query)
            }
            adapter.submitList(filtered)
        }
    }

    // cadastrar ou editar uma lista
    private fun showListDialog(list: ShoppingList?) {
        mainViewModel.setEditingList(list)
        val isEditing = list != null

        val input = EditText(this).apply {
            if (isEditing) {
                setText(list?.title)
            }
            mainViewModel.pendingImageUri.value?.let {
                // se houver uma imagem pendente, ela será usada
            }
        }

        val title = if (isEditing) "Editar lista" else "Nova lista"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Salvar") { d, _ ->
                val listTitle = input.text.toString().trim()
                if (listTitle.isNotEmpty()) {
                    if (isEditing) {
                        list?.let { currentList ->
                            currentList.title = listTitle
                            mainViewModel.pendingImageUri.value?.let {
                                currentList.imageUri = it.toString()
                            }
                        }
                    } else {
                        val newList = ShoppingList(
                            id = FakeDatabase.nextListId(),
                            title = listTitle,
                            imageUri = mainViewModel.pendingImageUri.value?.toString()
                        )
                        FakeDatabase.shoppingLists.add(newList)
                    }
                    sortByTitle()
                    refresh()
                }
                d.dismiss()
            }
            .setNeutralButton("Imagem…") { d, _ ->
                pickImage.launch("image/*")
                d.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
    private fun sortByTitle() {
        FakeDatabase.shoppingLists.sortBy { it.title.lowercase() }
    }

    // atualiza o RecyclerView com os dados atuais
    private fun refresh() {
        adapter.submitList(FakeDatabase.shoppingLists.toList())
    }

    // sempre que a tela volta a aparecer ele ordena e atualiza
    override fun onResume() {
        super.onResume()
        sortByTitle()
        refresh()
    }
}
