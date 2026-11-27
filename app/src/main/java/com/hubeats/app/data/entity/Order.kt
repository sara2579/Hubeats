package com.hubeats.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Restaurant::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("restaurantId")]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val restaurantId: Int,
    val studentName: String,
    val restaurantName: String,
    val itemsSummary: String,
    val status: String,
    val total: Double,
    val placedAt: Long,
    val inProgressAt: Long? = null,
    val readyAt: Long? = null,
    val pickedUpAt: Long? = null
)