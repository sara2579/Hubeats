package com.hubeats.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hubeats.app.data.entity.Order

@Dao
interface OrderDao {


    @Insert
    suspend fun insert(order: Order): Long

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)


    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY placedAt DESC")
    fun getForStudent(userId: Int): LiveData<List<Order>>


    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY placedAt DESC")
    fun getForRestaurant(restaurantId: Int): LiveData<List<Order>>


    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId AND status = :status ORDER BY placedAt DESC")
    fun getByStatus(restaurantId: Int, status: String): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getById(orderId: Int): LiveData<Order?>


    @Query("SELECT COUNT(*) FROM orders WHERE restaurantId = :restaurantId AND status = :status")
    fun countByStatus(restaurantId: Int, status: String): LiveData<Int>
}