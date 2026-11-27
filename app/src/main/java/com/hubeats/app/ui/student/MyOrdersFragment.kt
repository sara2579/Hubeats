package com.hubeats.app.ui.student

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hubeats.app.R

// My Orders screen (GAMAR).
// Fragment(R.layout.fragment_my_orders) = "this screen uses that layout file".
class MyOrdersFragment : Fragment(R.layout.fragment_my_orders) {

    // onViewCreated runs right after the layout is on screen: the place to set things up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TEMPORARY (until Stage 6): no data yet, so show the empty message
        view.findViewById<TextView>(R.id.text_empty).visibility = View.VISIBLE
    }
}