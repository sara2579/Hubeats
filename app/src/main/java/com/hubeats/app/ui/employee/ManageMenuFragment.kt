package com.hubeats.app.ui.employee

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import com.hubeats.app.R
import com.hubeats.app.util.SessionManager
import com.hubeats.app.viewmodel.ManageMenuViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

class ManageMenuFragment : Fragment(R.layout.fragment_manage_menu) {

    private lateinit var viewModel: ManageMenuViewModel
    private lateinit var adapter: ManageMenuAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmptyMenu: TextView
    private lateinit var btnAddMenuItem: Button

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerManageMenu)
        textEmptyMenu = view.findViewById(R.id.textEmptyMenu)
        btnAddMenuItem = view.findViewById(R.id.btnAddMenuItem)

        adapter = ManageMenuAdapter(
            onEdit = { item ->
                openEditScreen(item.id)
            },
            onDelete = { item ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Menu Item")
                    .setMessage("Are you sure you want to delete ${item.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteMenuItem(item)

                        Toast.makeText(
                            requireContext(),
                            "${item.name} deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[ManageMenuViewModel::class.java]

        val sessionManager = SessionManager(requireContext())
        val restaurantId = sessionManager.getRestaurantId()

        if (restaurantId == null) {
            Toast.makeText(
                requireContext(),
                "No restaurant is linked to this employee account.",
                Toast.LENGTH_LONG
            ).show()

            btnAddMenuItem.isEnabled = false
            return
        }

        viewModel.getMenu(restaurantId).observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)

            if (items.isEmpty()) {
                textEmptyMenu.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                textEmptyMenu.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        btnAddMenuItem.setOnClickListener {
            openEditScreen(-1)
        }
    }

    private fun openEditScreen(menuItemId: Int) {
        val bundle = Bundle().apply {
            putInt("menuItemId", menuItemId)
        }

        // The edit destination will be connected through
        // the employee navigation graph.
        parentFragmentManager.setFragmentResult(
            "openEditMenuItem",
            bundle
        )
    }
}