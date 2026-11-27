package com.hubeats.app.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hubeats.app.R
import com.hubeats.app.data.entity.Restaurant

/** Shows one card per restaurant. Tapping a card calls onClick(restaurant). */
class RestaurantAdapter(
    private val onClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.imageLogo)
        val name: TextView = view.findViewById(R.id.textName)
        val description: TextView = view.findViewById(R.id.textDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.name.text = restaurant.name
        holder.description.text = restaurant.description

        // logoName is the drawable's file name, e.g. "logo_hardees". Falls back to a generic icon.
        val context = holder.itemView.context
        val logoRes = context.resources
            .getIdentifier(restaurant.logoName, "drawable", context.packageName)
        holder.logo.setImageResource(if (logoRes != 0) logoRes else R.drawable.ic_restaurant)

        holder.itemView.setOnClickListener { onClick(restaurant) }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(old: Restaurant, new: Restaurant) = old.id == new.id
        override fun areContentsTheSame(old: Restaurant, new: Restaurant) = old == new
    }
}