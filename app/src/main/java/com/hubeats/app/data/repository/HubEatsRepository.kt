package com.hubeats.app.data.repository

import androidx.lifecycle.LiveData
import com.hubeats.app.data.database.AppDatabase
import com.hubeats.app.data.database.SampleData
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.entity.Order
import com.hubeats.app.data.entity.OrderItem
import com.hubeats.app.data.entity.OrderStatus
import com.hubeats.app.data.entity.Restaurant
import com.hubeats.app.data.entity.User

class HubEatsRepository(private val db: AppDatabase) {

    suspend fun addSampleDataIfEmpty() {
        if (db.userDao().count() == 0) {
            SampleData.insert(db)
        }
    }


    suspend fun login(username: String, password: String): User? {
        return db.userDao().login(username, password)
    }


    fun getAllRestaurants(): LiveData<List<Restaurant>> {
        return db.restaurantDao().getAll()
    }

    fun searchRestaurants(text: String): LiveData<List<Restaurant>> {
        return db.restaurantDao().search(text)
    }


    fun getMenu(restaurantId: Int): LiveData<List<MenuItem>> {
        return db.menuItemDao().getMenu(restaurantId)
    }

    suspend fun getMenuItem(id: Int): MenuItem? {
        return db.menuItemDao().getById(id)
    }

    suspend fun addMenuItem(item: MenuItem) {
        db.menuItemDao().insert(item)
    }

    suspend fun updateMenuItem(item: MenuItem) {
        db.menuItemDao().update(item)
    }

    suspend fun deleteMenuItem(item: MenuItem) {
        db.menuItemDao().delete(item)
    }


    suspend fun placeOrder(order: Order, items: List<OrderItem>): Long {
        val newOrderId = db.orderDao().insert(order)
        val itemsWithOrderId = items.map { it.copy(orderId = newOrderId.toInt()) }
        db.orderItemDao().insertAll(itemsWithOrderId)
        return newOrderId
    }

    suspend fun cancelOrder(order: Order) {
        db.orderDao().delete(order)
    }

    fun getOrdersForStudent(userId: Int): LiveData<List<Order>> {
        return db.orderDao().getForStudent(userId)
    }

    fun getOrdersForRestaurant(restaurantId: Int): LiveData<List<Order>> {
        return db.orderDao().getForRestaurant(restaurantId)
    }

    fun getOrdersByStatus(restaurantId: Int, status: String): LiveData<List<Order>> {
        return db.orderDao().getByStatus(restaurantId, status)
    }

    fun countOrdersByStatus(restaurantId: Int, status: String): LiveData<Int> {
        return db.orderDao().countByStatus(restaurantId, status)
    }

    fun getOrder(orderId: Int): LiveData<Order?> {
        return db.orderDao().getById(orderId)
    }

    fun getOrderItems(orderId: Int): LiveData<List<OrderItem>> {
        return db.orderItemDao().getForOrder(orderId)
    }

    suspend fun updateOrderStatus(order: Order, newStatus: String) {
        val now = System.currentTimeMillis()
        val updatedOrder = when (newStatus) {
            OrderStatus.IN_PROGRESS -> order.copy(status = newStatus, inProgressAt = now)
            OrderStatus.READY -> order.copy(status = newStatus, readyAt = now)
            OrderStatus.PICKED_UP -> order.copy(status = newStatus, pickedUpAt = now)
            else -> order.copy(status = newStatus)
        }
        db.orderDao().update(updatedOrder)
    }
}
