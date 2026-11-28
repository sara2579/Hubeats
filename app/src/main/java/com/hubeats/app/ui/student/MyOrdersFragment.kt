package com.hubeats.app.ui.student

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels                  // gets this screen's ViewModel (and keeps it on rotation)
import androidx.recyclerview.widget.RecyclerView         // the scrolling list
import com.hubeats.app.R
import com.hubeats.app.util.SessionManager               // Salam's: knows who is logged in
import com.hubeats.app.viewmodel.StudentOrdersViewModel  // your ViewModel (Stage 6.1)
import com.hubeats.app.viewmodel.ViewModelFactory        // builds the ViewModel and gives it the repository

// My Orders screen (GAMAR): the logged-in student's orders, newest first.
// Fragment(R.layout.fragment_my_orders) = "this screen uses that layout file".
class MyOrdersFragment : Fragment(R.layout.fragment_my_orders) {

    // "by viewModels" gets this screen's ViewModel. On rotation you get the SAME one back.
    // The factory is what gives it the repository.
    private val viewModel: StudentOrdersViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    // onViewCreated runs right after the layout is on screen: the place to set things up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Find the views in fragment_my_orders.xml
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_orders)
        val textEmpty = view.findViewById<TextView>(R.id.text_empty)

        // 2. Make the adapter (it handles taps on cards itself) and connect it to the list
        val adapter = OrderAdapter()
        recycler.adapter = adapter

        // 3. Tell the ViewModel who is logged in (Salam's SessionManager knows)
        val userId = SessionManager(requireContext()).getUserId()
        viewModel.setUserId(userId)

        // 4. Observe = "whenever myOrders changes, run this block".
        //    viewLifecycleOwner = stop watching when this screen closes (no leaks/crashes).
        viewModel.myOrders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders.sortedByDescending { it.placedAt })   // newest first

            // Show the empty message only when there are no orders
            if (orders.isEmpty()) {
                textEmpty.visibility = View.VISIBLE
            } else {
                textEmpty.visibility = View.GONE
            }
        }
    }
}