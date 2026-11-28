package com.hubeats.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hubeats.app.data.entity.MenuItem

@Dao
interface MenuItemDao {

    @Insert
    suspend fun insert(item: MenuItem)

    @Update
    suspend fun update(item: MenuItem)

    @Delete
    suspend fun delete(item: MenuItem)

    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId ORDER BY name")
    fun getMenu(restaurantId: Int): LiveData<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getById(id: Int): MenuItem?
}