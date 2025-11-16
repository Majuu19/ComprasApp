package com.example.bruno.ui.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno.databinding.ItemShoppingItemBinding
import com.example.bruno.model.Item

class ItemAdapter(
    private val onToggleBought: (Item) -> Unit,
    private val onEdit: (Item) -> Unit,
    private val onDelete: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.checkBought.setOnClickListener {

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onToggleBought(getItem(adapterPosition))
                }
            }

            binding.btnEditItem.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEdit(getItem(adapterPosition))
                }
            }

            binding.btnDeleteItem.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDelete(getItem(adapterPosition))
                }
            }
        }


        fun bind(item: Item) {
            binding.txtItemName.text = item.name
            binding.txtQuantity.text = "${item.quantity} ${item.unit}"
            binding.checkBought.isChecked = item.bought
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }
}
