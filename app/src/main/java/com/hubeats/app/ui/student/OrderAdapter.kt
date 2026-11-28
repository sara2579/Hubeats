package com.hubeats.app.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf                  // packs the order number to send to Order details
import androidx.navigation.findNavController      // lets the card find the navigation "driver"
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.hubeats.app.R
import com.hubeats.app.data.entity.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Turns a time saved as a number (milliseconds) into text like "11:05 AM".
// It's outside the class so OrderDetailFragment can use it too.
// pattern has a default value, HOW to write it. "= h:mm a" is a default
fun formatTime(millis: Long, pattern: String = "h:mm a"): String {
    // Read from the inside out:
    // 1. Date(millis)            -> turn the number into a date object
    // 2. SimpleDateFormat(...)   -> a formatter that writes dates using our pattern
    //    Locale.getDefault()     -> in the phone's own language style (an Arabic phone shows
    //                               Arabic digits and month names; the time itself is the same)
    // 3. .format(...)            -> write it as text, which we return
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
}

// Shows the student's orders in the My Orders list (GAMAR).
// Tapping a card opens Order details by itself, so the fragment doesn't need to pass anything in.
class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // The orders currently shown. Starts empty.
    private var orders: List<Order> = emptyList()

    // The fragment calls this when new data arrives
    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()   // "the data changed, redraw the list"
    }

    // A ViewHolder remembers the views inside ONE row, so we find them only once
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNumber: TextView = view.findViewById(R.id.text_order_number)
        val chipStatus: Chip = view.findViewById(R.id.chip_status)
        val textRestaurantTime: TextView = view.findViewById(R.id.text_restaurant_time)
    }

    // 1. "Make me an empty row": the RecyclerView calls this by itself when it needs a new card.
    //    It only BUILDS the empty card. Filling it with an order happens in onBindViewHolder (#3).

    // parent : Each card lives inside the RecyclerView so the RecyclerView is their parent.
    // When the RecyclerView calls onCreateViewHolder(parent, …),
    // it's saying: "Make me a new card, and btw I'm the one it'll go inside."
    // viewType = for lists with different kinds of rows; we only have one

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        // "Inflate" = turn an XML file into a real card the app can show.
        // item_order.xml is only a description; this BUILDS the card from it.
        val view = LayoutInflater.from(parent.context)   // Android's XML-to-screen builder
            .inflate(
                R.layout.item_order,   // which XML to build
                parent,                // it will go inside the RecyclerView (so sizes are measured right)
                false                  // but DON'T add it yet; the RecyclerView adds it itself (true would crash)
            )
        //view is the card we just built from item_order.xml.
        //OrderViewHolder(view) puts the card inside a ViewHolder. As that happens, the 3 findViewById lines run once and save the views.
        //The RecyclerView then keeps this card + saved views together as one package and reuses it while scrolling

        return OrderViewHolder(view)
    }

    // 2. "How many rows?"
    override fun getItemCount(): Int = orders.size

    // 3. "Fill row number [position]" with that order's data.
    //    Runs every time a card needs data: when the list first shows,
    //    and again each time a card is reused while scrolling.
    // holder   = the card to fill, with its 3 saved views (from #1)
    // position = which row this card is for; counting starts at 0 (top row = 0)

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        // Get the order for this row from our list (orders[0] = the newest)
        val order = orders[position]

        // itemView = the whole card. Its context lets us read text from strings.xml
        // (an adapter isn't a fragment, so it can't call getString() directly)
        val context = holder.itemView.context

        // "Order #%d" from strings.xml, with order.id filling the %d -> "Order #4"
        holder.textNumber.text = context.getString(R.string.order_number, order.id)

        // The status ("Placed", "In Progress"...) goes in the chip
        holder.chipStatus.text = order.status

        // Join 3 pieces of text with +: restaurant + " · " + time -> "Hardee's · 27 Nov, 11:05 AM"
        holder.textRestaurantTime.text =
            order.restaurantName + " · " + formatTime(order.placedAt, "d MMM, h:mm a")

        // When the WHOLE card is tapped, open Order details for THIS order.
        // Only the navController can change screens. The card sits inside the NavHostFragment,
        // so it can look up through the boxes it's in and find the navController itself.
        holder.itemView.setOnClickListener {
            val args = bundleOf("orderId" to order.id)                 // send the order number (Order details reads "orderId")
            holder.itemView.findNavController()                        // the card finds the navigation "driver"
                .navigate(R.id.action_myOrders_to_orderDetail, args)   // follow the arrow (from nav_student.xml) to Order details
        }
    }
}