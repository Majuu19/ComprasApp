package com.example.bruno.ui.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno.R
import com.example.bruno.databinding.ItemShoppingItemBinding
import com.example.bruno.model.Category
import com.example.bruno.model.Item

class ItemListAdapter(
    private val onCheck: (Item) -> Unit,
    private val onEdit: (Item) -> Unit,
    private val onDelete: (Item) -> Unit
) : ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateData(newList: List<Item>) {
        submitList(newList) {
            notifyDataSetChanged()
        }
    }


    inner class ViewHolder(private val binding: ItemShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.txtItemName.text = item.name
            binding.txtQuantity.text = "${item.quantity} ${item.unit}"
            binding.checkBought.isChecked = item.isBought

            val categoryIcon = when (item.category) {
                Category.FRUTAS -> R.drawable.ic_category_frutas
                Category.CARNES -> R.drawable.ic_category_carnes
                Category.BEBIDAS -> R.drawable.ic_category_bebidas
                Category.HIGIENE -> R.drawable.ic_category_higiene
                Category.LIMPEZA -> R.drawable.ic_category_limpeza
                Category.PADARIA -> R.drawable.ic_category_padaria
                Category.OUTROS -> R.drawable.ic_category_outros
            }
            binding.imgCategory.setImageResource(categoryIcon)

            binding.checkBought.setOnClickListener { onCheck(item) }
            binding.btnEditItem.setOnClickListener { onEdit(item) }
            binding.btnDeleteItem.setOnClickListener { onDelete(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}
