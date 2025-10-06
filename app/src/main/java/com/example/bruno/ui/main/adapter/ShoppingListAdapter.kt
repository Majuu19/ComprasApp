package com.example.bruno.ui.main.adapter
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno.databinding.ItemShoppingListBinding
import com.example.bruno.model.ShoppingList


class ShoppingListAdapter(

    private val onClick: (ShoppingList) -> Unit,
    private val onEdit: (ShoppingList) -> Unit,
    private val onDelete: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListAdapter.ViewHolder>(DiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(private val binding: ItemShoppingListBinding) : // classe interna que representa cada item visiivel do Recycler
        RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ShoppingList) {
            binding.txtTitle.text = list.title

            if (list.imageUri != null) {// se tiver imagem salva carrega a URI se não mostra incone padrao
                binding.imgList.setImageURI(Uri.parse(list.imageUri))
            } else {
                binding.imgList.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // define ações de clique um clicar no item abre a lista o outro clicar no botão de deletar remove a lista
            binding.root.setOnClickListener { onClick(list) }
            binding.btnEdit.setOnClickListener { onEdit(list) }
            binding.btnDelete.setOnClickListener { onDelete(list) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShoppingList>() {

        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
            return oldItem == newItem
        }
    }
}
