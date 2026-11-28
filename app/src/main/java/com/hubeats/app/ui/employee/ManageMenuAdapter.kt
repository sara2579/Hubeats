package com.hubeats.app.ui.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hubeats.app.R
import com.hubeats.app.data.entity.MenuItem

class ManageMenuAdapter (
    private val onEdit: (MenuItem) -> Unit,
    private val onDelete: (MenuItem) -> Unit
) : RecyclerView.Adapter<ManageMenuAdapter.MenuViewHolder>() {

    private var menuItems = emptyList<MenuItem>()

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textItemName: TextView = itemView.findViewById(R.id.textItemName)
        val textItemPrice: TextView = itemView.findViewById(R.id.textItemPrice)
        val btnEditItem: Button = itemView.findViewById(R.id.btnEditItem)
        val btnDeleteItem: Button = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_menu, parent, false)

        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuItems[position]

        holder.textItemName.text = item.name
        holder.textItemPrice.text = "AED %.2f".format(item.price)
        holder.btnEditItem.setOnClickListener { onEdit(item) }
        holder.btnDeleteItem.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = menuItems.size
    fun submitList(items: List<MenuItem>) {
        menuItems = items
        notifyDataSetChanged()
    }
}