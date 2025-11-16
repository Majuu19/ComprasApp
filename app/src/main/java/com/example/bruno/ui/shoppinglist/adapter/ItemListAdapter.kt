package com.example.bruno.ui.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno.databinding.ItemCategoryHeaderBinding
import com.example.bruno.databinding.ItemShoppingItemBinding
import com.example.bruno.model.Category
import com.example.bruno.model.Item
import com.example.bruno.model.ListItem
import java.lang.ClassCastException

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ItemListAdapter(
    private val onCheck: (Item) -> Unit,
    private val onEdit: (Item) -> Unit,
    private val onDelete: (Item) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.HeaderItem -> ITEM_VIEW_TYPE_HEADER
            is ListItem.ShoppingItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            ITEM_VIEW_TYPE_ITEM -> {
                val binding = ItemShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = getItem(position) as ListItem.HeaderItem
                holder.bind(headerItem)
            }
            is ItemViewHolder -> {
                val shoppingItem = getItem(position) as ListItem.ShoppingItem
                holder.bind(shoppingItem.item)
            }
        }
    }

    class HeaderViewHolder(private val binding: ItemCategoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: ListItem.HeaderItem) {
            binding.txtCategoryHeader.text = header.category
        }
    }

    inner class ItemViewHolder(private val binding: ItemShoppingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkBought.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val shoppingItem = getItem(adapterPosition) as ListItem.ShoppingItem
                    onCheck(shoppingItem.item)
                }
            }
            binding.btnEditItem.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val shoppingItem = getItem(adapterPosition) as ListItem.ShoppingItem
                    onEdit(shoppingItem.item)
                }
            }
            binding.btnDeleteItem.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val shoppingItem = getItem(adapterPosition) as ListItem.ShoppingItem
                    onDelete(shoppingItem.item)
                }
            }
        }

        fun bind(item: Item) {
            binding.txtItemName.text = item.name
            binding.txtQuantity.text = "${item.quantity} ${item.unit}"
            binding.checkBought.isChecked = item.bought
            val category = Category.fromString(item.category)
            binding.imgCategory.setImageResource(category.getIcon())
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.HeaderItem && newItem is ListItem.HeaderItem -> oldItem.id == newItem.id
                oldItem is ListItem.ShoppingItem && newItem is ListItem.ShoppingItem -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.HeaderItem && newItem is ListItem.HeaderItem -> oldItem == newItem
                oldItem is ListItem.ShoppingItem && newItem is ListItem.ShoppingItem -> oldItem.item == newItem.item
                else -> false
            }
        }
    }
}
