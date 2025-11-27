package com.example.bruno.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.bruno.R
import com.example.bruno.databinding.ActivityMainBinding
import com.example.bruno.model.ShoppingList
import com.example.bruno.ui.login.LoginActivity
import com.example.bruno.ui.main.adapter.ShoppingListAdapter
import com.example.bruno.ui.shoppinglist.ShoppingListActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels { MainVMFactory(this) }
    private lateinit var adapter: ShoppingListAdapter

    private var loadingDialog: AlertDialog? = null
    private val auth = FirebaseAuth.getInstance()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.setPendingImageUri(uri)
            showListDialog(vm.editingList.value, uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        setupListeners()
        setupObservers()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            goToLogin()
            return
        }
        vm.start()
    }

    override fun onStop() {
        super.onStop()
        vm.stop()
        loadingDialog?.dismiss()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupRecycler() {
        adapter = ShoppingListAdapter(
            onClick = { list ->
                val it = Intent(this, ShoppingListActivity::class.java)
                it.putExtra(ShoppingListActivity.EXTRA_LIST_ID, list.id)
                startActivity(it)
            },
            onEdit = { list -> showListDialog(list, null) },
            onDelete = { list -> vm.deleteList(list) }
        )

        binding.recyclerShoppingLists.layoutManager = LinearLayoutManager(this)
        binding.recyclerShoppingLists.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddList.setOnClickListener {
            showListDialog(null, null)
        }

        binding.inputSearchLists.addTextChangedListener { text ->
            vm.setQuery(text?.toString().orEmpty())
        }
    }

    private fun setupObservers() {
        vm.lists.observe(this) { list ->
            adapter.submitList(list)
        }

        vm.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                if (loadingDialog == null) {
                    loadingDialog = AlertDialog.Builder(this)
                        .setTitle("Aguarde")
                        .setMessage("Salvando lista...")
                        .setCancelable(false)
                        .create()
                }
                loadingDialog?.show()
            } else {
                loadingDialog?.dismiss()
            }
        }
    }

    private fun showListDialog(list: ShoppingList?, imageUri: Uri?) {
        vm.setEditingList(list)

        val input = EditText(this).apply {
            hint = "Título da lista"
            list?.let { setText(it.title) }
        }

        val imageView = ImageView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                200
            ).also { it.setMargins(0, 16, 0, 0) }

            val imageToLoad = imageUri ?: list?.imageUrl
            if (imageToLoad != null) {
                load(imageToLoad)
            } else {
                load(R.drawable.ic_outros)
            }
        }

        val dialogLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
            addView(input)
            addView(imageView)
        }

        AlertDialog.Builder(this)
            .setTitle(if (list != null) "Editar lista" else "Nova lista")
            .setView(dialogLayout)
            .setPositiveButton("Salvar") { dialog, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    vm.saveList(title)
                }
                dialog.dismiss()
            }
            .setNeutralButton("Imagem…") { dialog, _ ->
                pickImage.launch("image/*")
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
}
