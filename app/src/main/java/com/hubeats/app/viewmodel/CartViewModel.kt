package com.hubeats.app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.entity.Order
import com.hubeats.app.data.entity.OrderItem
import com.hubeats.app.data.entity.OrderStatus
import com.hubeats.app.data.repository.HubEatsRepository
import kotlinx.coroutines.launch

data class CartItem(val menuItem: MenuItem, val quantity: Int)

/**
 * The cart. Shared by MenuFragment and CartFragment (use activityViewModels),
 * so it survives rotation and moving between screens. Holds one restaurant only.
 */
class CartViewModel(private val repository: HubEatsRepository) : ViewModel() {

    private val _items = MutableLiveData<List<CartItem>>(emptyList())
    val items: LiveData<List<CartItem>> = _items

    val total: LiveData<Double> = _items.map { list ->
        list.sumOf { it.menuItem.price * it.quantity }
    }
    val itemCount: LiveData<Int> = _items.map { list ->
        list.sumOf { it.quantity }
    }

    /** Set after an order is saved; the screen shows the dialog, then calls orderPlacedHandled(). */
    private val _placedOrderId = MutableLiveData<Long?>(null)
    val placedOrderId: LiveData<Long?> = _placedOrderId

    private var restaurantId = -1
    private var restaurantName = ""
    private var isPlacing = false

    /** Called by MenuFragment. Opening a different restaurant empties the cart. */
    fun startRestaurant(id: Int, name: String) {
        if (id != restaurantId) {
            _items.value = emptyList()
            restaurantId = id
        }
        restaurantName = name
    }

    fun quantityOf(menuItemId: Int): Int =
        _items.value.orEmpty().firstOrNull { it.menuItem.id == menuItemId }?.quantity ?: 0

    fun add(item: MenuItem) {
        val list = _items.value.orEmpty()
        val existing = list.firstOrNull { it.menuItem.id == item.id }
        _items.value = if (existing == null) {
            list + CartItem(item, 1)
        } else {
            list.map {
                if (it.menuItem.id == item.id) it.copy(quantity = it.quantity + 1) else it
            }
        }
    }

    fun remove(item: MenuItem) {
        val list = _items.value.orEmpty()
        val existing = list.firstOrNull { it.menuItem.id == item.id } ?: return
        _items.value = if (existing.quantity <= 1) {
            list.filter { it.menuItem.id != item.id }
        } else {
            list.map {
                if (it.menuItem.id == item.id) it.copy(quantity = it.quantity - 1) else it
            }
        }
    }

    /** Saves the order (status Placed) and its items, then empties the cart. */
    fun placeOrder(userId: Int, studentName: String) {
        val list = _items.value.orEmpty()
        if (list.isEmpty() || isPlacing) return
        isPlacing = true

        val order = Order(
            userId = userId,
            restaurantId = restaurantId,
            studentName = studentName,
            restaurantName = restaurantName,
            itemsSummary = list.joinToString(", ") { "${it.quantity}x ${it.menuItem.name}" },
            status = OrderStatus.PLACED,
            total = list.sumOf { it.menuItem.price * it.quantity },
            placedAt = System.currentTimeMillis()
        )
        val orderItems = list.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                itemName = it.menuItem.name,
                price = it.menuItem.price,
                quantity = it.quantity
            )
        }

        viewModelScope.launch {
            val newOrderId = repository.placeOrder(order, orderItems)
            _items.value = emptyList()
            restaurantId = -1
            isPlacing = false
            _placedOrderId.value = newOrderId
        }
    }

    fun orderPlacedHandled() {
        _placedOrderId.value = null
    }
}