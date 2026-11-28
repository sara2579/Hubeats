package com.hubeats.app.ui.employee

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hubeats.app.R
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.util.SessionManager
import com.hubeats.app.viewmodel.ManageMenuViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

class EditMenuItemFragment :
    Fragment(R.layout.fragment_edit_menu_item) {

    private lateinit var viewModel: ManageMenuViewModel
    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var textTitle: TextView
    private lateinit var textError: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var menuItemId: Int = -1
    private var restaurantId: Int = -1

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        editName = view.findViewById(R.id.editMenuItemName)
        editPrice = view.findViewById(R.id.editMenuItemPrice)
        textTitle = view.findViewById(R.id.textEditMenuTitle)
        textError = view.findViewById(R.id.textMenuItemError)
        btnSave = view.findViewById(R.id.btnSaveMenuItem)
        btnCancel = view.findViewById(R.id.btnCancelMenuItem)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[ManageMenuViewModel::class.java]

        menuItemId = arguments?.getInt("menuItemId", -1) ?: -1
        restaurantId = SessionManager(requireContext())
            .getRestaurantId() ?: -1

        if (restaurantId == -1) {
            showError("No restaurant is linked to this employee account.")
            btnSave.isEnabled = false
            return
        }

        if (menuItemId == -1) {
            textTitle.text = "Add Menu Item"
            btnSave.text = "Add Item"
        } else {
            textTitle.text = "Edit Menu Item"
            btnSave.text = "Save Changes"
            viewModel.loadMenuItem(menuItemId)
        }

        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            if (item != null && item.id == menuItemId) {
                editName.setText(item.name)
                editPrice.setText(item.price.toString())
            }
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            when (success) {
                true -> {
                    Toast.makeText(
                        requireContext(),
                        "Menu item saved successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    viewModel.resetSaveResult()
                    parentFragmentManager.popBackStack()
                }

                false -> showError("Unable to save the menu item. Please try again.")
                null -> Unit
            }
        }

        btnSave.setOnClickListener {
            saveItem()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveItem() {
        val name = editName.text.toString().trim()
        val priceText = editPrice.text.toString().trim()
        val price = priceText.toDoubleOrNull()

        when {
            name.isEmpty() -> {
                showError("Please enter an item name.")
            }

            price == null || price <= 0.0 -> {
                showError("Please enter a valid price greater than zero.")
            }

            else -> {
                textError.visibility = View.GONE

                val item = MenuItem(
                    id = if (menuItemId == -1) 0 else menuItemId,
                    restaurantId = restaurantId,
                    name = name,
                    price = price
                )

                viewModel.saveMenuItem(
                    item,
                    isNew = menuItemId == -1
                )
            }
        }
    }

    private fun showError(message: String) {
        textError.text = message
        textError.visibility = View.VISIBLE
    }
}