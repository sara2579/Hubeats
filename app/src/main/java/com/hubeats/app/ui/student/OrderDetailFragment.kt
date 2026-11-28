package com.hubeats.app.ui.student

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hubeats.app.R
import com.hubeats.app.data.entity.Order
import com.hubeats.app.data.entity.OrderStatus
import com.hubeats.app.viewmodel.StudentOrdersViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

// Order details + timeline (GAMAR). Updates live when the employee changes the status.
class OrderDetailFragment : Fragment(R.layout.fragment_order_detail) {

    private val viewModel: StudentOrdersViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Read the orderId that My Orders sent us (see nav_student.xml <argument>)
        val orderId = requireArguments().getInt("orderId")
        viewModel.setOrderId(orderId)

        // 2. Find the views
        val textNumber = view.findViewById<TextView>(R.id.text_order_number)
        val textRestaurant = view.findViewById<TextView>(R.id.text_restaurant)
        val textItems = view.findViewById<TextView>(R.id.text_items)
        val textTotal = view.findViewById<TextView>(R.id.text_total)
        val buttonCancel = view.findViewById<MaterialButton>(R.id.button_cancel)

        // The 4 included timeline steps (each is a copy of item_timeline_step.xml)
        val stepPlaced = view.findViewById<View>(R.id.step_placed)
        val stepInProgress = view.findViewById<View>(R.id.step_in_progress)
        val stepReady = view.findViewById<View>(R.id.step_ready)
        val stepPickedUp = view.findViewById<View>(R.id.step_picked_up)

        // 3. Watch the order. Runs now, and again every time it changes in the database.
        viewModel.order.observe(viewLifecycleOwner) { order ->

            // null = the order no longer exists (it was cancelled), so go back to My Orders
            if (order == null) {
                findNavController().popBackStack()
                return@observe   // stop this block here
            }

            // --- The top info ---
            // (we can call getString() directly here because this is a fragment)
            textNumber.text = getString(R.string.order_number, order.id)     // "Order #%d" + id -> "Order #4"
            textRestaurant.text = order.restaurantName                        // -> "Hardee's"
            textTotal.text = getString(R.string.order_total, order.total)    // "Total: %.2f AED" -> "Total: 32.05 AED"

            // --- The 4 timeline steps ---
            // The layout includes item_timeline_step.xml 4 times; showStep() (further down) fills ONE of them.
            // We give it: which row, the step's name, and when it happened (null = hasn't happened yet).
            // A step is "reached" when its time is not null: Sarah's Order saves each time only when it happens
            // (e.g. inProgressAt is filled in when the employee taps Start).
            // Reached -> real time, full strength.  Not yet -> "—" and the whole row faded.
            showStep(stepPlaced, R.string.step_placed, order.placedAt)
            showStep(stepInProgress, R.string.step_in_progress, order.inProgressAt)
            showStep(stepReady, R.string.step_ready, order.readyAt)
            showStep(stepPickedUp, R.string.step_picked_up, order.pickedUpAt)

            // --- Hide the last line ---
            // Each step has a thin line going down to the next one. Picked Up is the last step,
            // so we find the line INSIDE that row only and hide it (GONE = invisible and takes no space).
            stepPickedUp.findViewById<View>(R.id.step_line).visibility = View.GONE

            // Cancel is only allowed while the order is still "Placed"
            if (order.status == OrderStatus.PLACED) {
                buttonCancel.visibility = View.VISIBLE
                buttonCancel.setOnClickListener { askToCancel(order) }
            } else {
                buttonCancel.visibility = View.GONE
            }
        }

        // 4. Watch the items and show one per line: "1x Burger", "2x Fries"
        //    The items come from a different table (order_items) than the order itself,
        //    so they have their own LiveData and their own observe block.
        //    One TextView with a line per item is simpler than another list + adapter.
        viewModel.orderItems.observe(viewLifecycleOwner) { items ->   // runs when the items arrive or change
            // { item -> ... } turns EACH item into a line of text.
            // joinToString("\n") sticks all the lines together with a new line (\n) between them:
            //   "1x Burger" + "2x Fries" -> "1x Burger\n2x Fries" (shows as 2 lines)
            textItems.text = items.joinToString("\n") { item ->
                "${item.quantity}x ${item.itemName}"
            }
        }
    }

    // Fills ONE timeline step: its name, its time, and whether it looks done or faded.
    // time is Long? (the ? means it can be null = "not reached yet").
    // The dot is always blue (set once in bg_timeline_dot.xml), so no colour code is needed here.
    private fun showStep(step: View, labelText: Int, time: Long?) {
        val label = step.findViewById<TextView>(R.id.step_label)
        val timeText = step.findViewById<TextView>(R.id.step_time)

        label.setText(labelText)

        // time != null means "not empty" = this step HAS happened.
        if (time != null) {
            // Reached: show the real time, and the whole row at full strength
            timeText.text = formatTime(time)   // default pattern "h:mm a" -> "11:05 AM"
            step.alpha = 1f                    // alpha = how see-through: 1 = fully solid
        } else {
            // Not reached: a dash, and fade the WHOLE row (dot, line, name and time together)
            timeText.text = getString(R.string.step_not_yet)   // "—"
            step.alpha = 0.4f                                  // 0.4 = mostly see-through = looks faded
        }
    }

    // Shows a "are you sure?" dialog before cancelling.(button only visible while the order is "Placed").
    // A "builder" sets the dialog up piece by piece: each .something(...) adds one part,
    // and .show() at the end puts it on screen.
    private fun askToCancel(order: Order) {
        MaterialAlertDialogBuilder(requireContext())                   // start a  pop-up for this screen
            .setMessage(R.string.cancel_order_question)                // the question: "Are you sure you want to cancel this order?"
            .setPositiveButton(R.string.yes_cancel) { _, _ ->          // the "Yes, cancel" button and what it does
                // { _, _ -> ... } runs when it's tapped. Android passes 2 things (the dialog, which button);
                // we don't need them, so we write _ ("ignore this").
                viewModel.cancel(order)   // the ViewModel does the database work
                // A small message at the bottom of the screen for a short time
                Toast.makeText(requireContext(), R.string.order_cancelled, Toast.LENGTH_SHORT).show()
                // No need to leave this screen ourselves: the order is deleted, so the "order" LiveData
                // becomes null and the observe block above goes back to My Orders.
            }
            .setNegativeButton(R.string.keep_order, null)   // "No, keep order": null = just close the dialog
            .show()                                         // build it and show it
    }
}