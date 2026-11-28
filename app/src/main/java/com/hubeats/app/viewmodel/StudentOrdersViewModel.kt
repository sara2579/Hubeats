package com.hubeats.app.viewmodel

// Lifecycle library (ViewModel + LiveData)
import androidx.lifecycle.LiveData          // data the screen can watch; it announces when it changes (read-only)
import androidx.lifecycle.MutableLiveData   // LiveData that WE can change (used for userId and orderId)
import androidx.lifecycle.ViewModel         // the base class: makes this class survive screen rotation
import androidx.lifecycle.switchMap         // "when the id changes, fetch new data for it"
import androidx.lifecycle.viewModelScope    // a place to run coroutines that stops when the ViewModel is destroyed

// Sarah's database files
import com.hubeats.app.data.entity.Order                  // one order (number, restaurant, status, times...)
import com.hubeats.app.data.entity.OrderItem              // one line in an order (e.g. 2x Fries)
import com.hubeats.app.data.repository.HubEatsRepository  // the only door to the data

// Coroutines
import kotlinx.coroutines.launch            // starts background work (used in cancel)

// Holds the data for My Orders and Order Detail (GAMAR).
// A ViewModel allows screen rotation, so the data isn't lost or reloaded.


//Each section has 3 parts:
//1. A box holding an id (MutableLiveData)	A sticky note saying  which student, or which order
//2. The data (switchMap)	A TV channel tuned to whatever's on the sticky note	Fetches the data for that id, and keeps updating
//3. A setter function (setUserId, setOrderId)	Writing on the sticky note	The fragment uses it to say which id

class StudentOrdersViewModel(private val repository: HubEatsRepository) : ViewModel() {

    // ---------- My Orders ----------

    // Which student we're showing. MutableLiveData = a value we can change.
    //The sticky note. It starts empty and will hold the logged-in student's id
    private val userId = MutableLiveData<Int>()

    // switchMap = "whenever userId changes, ask the repository for that student's orders".
    // The result is LiveData: it updates by itself when the database changes.
    val myOrders: LiveData<List<Order>> = userId.switchMap { id ->
        repository.getOrdersForStudent(id)
    }

    //writing on the sticky note
    // The fragment calls this to see which student is logged in.
    // if u rotate the phone the fragment is rebuilt and calls it again with the SAME id.
    // The if-check skips that, so the orders aren't loaded again for nothing.
    fun setUserId(id: Int) {
        if (userId.value != id) {   // is the new id DIFFERENT from what's on the note?
            userId.value = id       // only then write it
        }
    }


    // ---------- Order Detail ----------
    //same pattern/flow as above

    // Which order we're showing
    private val orderId = MutableLiveData<Int>()

    // The order itself. It becomes null if the order is deleted (cancelled).
    val order: LiveData<Order?> = orderId.switchMap { id ->
        repository.getOrder(id)
    }

    // The items in that order (e.g. 1x Burger, 2x Fries)
    val orderItems: LiveData<List<OrderItem>> = orderId.switchMap { id ->
        repository.getOrderItems(id)
    }

    fun setOrderId(id: Int) {
        if (orderId.value != id) {
            orderId.value = id
        }
    }

    // ---------- Cancel ----------

    fun cancel(order: Order) {     //fun cancel(order: Order) is called when the student taps Yes, cancel.
        viewModelScope.launch {    //means do in the background coz changing the database could freeze the screen if done on the main thread.
            repository.cancelOrder(order)  //asks Sarah's repository to cancel it.
        }

        //You don't need to update the screen afterwards.
        // The database changes, so the order LiveData updates (becomes null) by itself,
        // and the screen goes back to My Orders.

    }
}