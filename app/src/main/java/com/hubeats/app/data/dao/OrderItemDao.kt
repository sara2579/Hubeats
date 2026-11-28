package com.hubeats.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hubeats.app.data.entity.OrderItem

@Dao
interface OrderItemDao {

    @Insert
    suspend fun insertAll(items: List<OrderItem>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getForOrder(orderId: Int): LiveData<List<OrderItem>>
}