package com.hubeats.app.data.database

import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.entity.Restaurant
import com.hubeats.app.data.entity.User

object SampleData {

    suspend fun insert(db: AppDatabase) {

        val hardeesId = db.restaurantDao().insert(
            Restaurant(name = "Hardee's", description = "Burgers and fries", logoName = "logo_hardees")
        ).toInt()
        val boxId = db.restaurantDao().insert(
            Restaurant(name = "The Box", description = "Sandwiches, pasta and coffee", logoName = "logo_thebox")
        ).toInt()
        val healthyId = db.restaurantDao().insert(
            Restaurant(name = "Healthy Club", description = "Salads, bowls and juices", logoName = "logo_healthy")
        ).toInt()

        db.menuItemDao().insert(MenuItem(restaurantId = hardeesId, name = "Classic Burger", price = 18.0))
        db.menuItemDao().insert(MenuItem(restaurantId = hardeesId, name = "Fries", price = 7.0))
        db.menuItemDao().insert(MenuItem(restaurantId = hardeesId, name = "Chicken Fillet", price = 20.0))
        db.menuItemDao().insert(MenuItem(restaurantId = hardeesId, name = "Soft Drink", price = 5.0))

        db.menuItemDao().insert(MenuItem(restaurantId = boxId, name = "Club Sandwich", price = 16.0))
        db.menuItemDao().insert(MenuItem(restaurantId = boxId, name = "Pasta Box", price = 22.0))
        db.menuItemDao().insert(MenuItem(restaurantId = boxId, name = "Iced Latte", price = 14.0))

        db.menuItemDao().insert(MenuItem(restaurantId = healthyId, name = "Caesar Salad", price = 19.0))
        db.menuItemDao().insert(MenuItem(restaurantId = healthyId, name = "Protein Bowl", price = 28.0))
        db.menuItemDao().insert(MenuItem(restaurantId = healthyId, name = "Fresh Juice", price = 12.0))

        db.userDao().insert(User(username = "student1", password = "1234", fullName = "Student One", role = "student"))
        db.userDao().insert(User(username = "student2", password = "1234", fullName = "Student Two", role = "student"))

        db.userDao().insert(User(username = "hardees", password = "1234", fullName = "Hardee's Staff", role = "employee", restaurantId = hardeesId))
        db.userDao().insert(User(username = "thebox", password = "1234", fullName = "The Box Staff", role = "employee", restaurantId = boxId))
        db.userDao().insert(User(username = "healthy", password = "1234", fullName = "Healthy Club Staff", role = "employee", restaurantId = healthyId))
    }
}