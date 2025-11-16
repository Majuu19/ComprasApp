package com.example.bruno.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bruno.R
import com.example.bruno.databinding.ItemShoppingListBinding
import com.example.bruno.model.ShoppingList

class ShoppingListAdapter(
    private val onClick: (ShoppingList) -> Unit,
    private val onEdit: (ShoppingList) -> Unit,
    private val onDelete: (ShoppingList) -> Unit
) : ListAdapter<ShoppingList, ShoppingListAdapter.ShoppingListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShoppingListViewHolder(private val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClick(getItem(adapterPosition))
                }
            }
            binding.btnEdit.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEdit(getItem(adapterPosition))
                }
            }
            binding.btnDelete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDelete(getItem(adapterPosition))
                }
            }
        }

        fun bind(list: ShoppingList) {
            binding.txtTitle.text = list.title


            if (list.imageUrl != null) {
                binding.imgList.load(list.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_outros)
                }
            } else {

                binding.imgList.load(R.drawable.ic_outros)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
        override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList) = oldItem == newItem
    }
}
