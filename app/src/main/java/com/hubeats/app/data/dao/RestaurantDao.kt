package com.hubeats.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hubeats.app.data.entity.Restaurant

@Dao
interface RestaurantDao {


    @Insert
    suspend fun insert(restaurant: Restaurant): Long

    @Query("SELECT * FROM restaurants ORDER BY name")
    fun getAll(): LiveData<List<Restaurant>>


    @Query("SELECT * FROM restaurants WHERE name LIKE '%' || :text || '%' ORDER BY name")
    fun search(text: String): LiveData<List<Restaurant>>
}