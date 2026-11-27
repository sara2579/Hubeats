package com.hubeats.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hubeats.app.data.dao.MenuItemDao
import com.hubeats.app.data.dao.OrderDao
import com.hubeats.app.data.dao.OrderItemDao
import com.hubeats.app.data.dao.RestaurantDao
import com.hubeats.app.data.dao.UserDao
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.entity.Order
import com.hubeats.app.data.entity.OrderItem
import com.hubeats.app.data.entity.Restaurant
import com.hubeats.app.data.entity.User

@Database(
    entities = [User::class, Restaurant::class, MenuItem::class, Order::class, OrderItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hubeats_db"
                ).build()
            }
            return INSTANCE!!
        }
    }
}