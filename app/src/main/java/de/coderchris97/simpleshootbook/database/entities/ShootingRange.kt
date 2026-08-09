package de.coderchris97.simpleshootbook.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("shooting_ranges")
data class ShootingRange(
    @PrimaryKey(true)
    @ColumnInfo("shooting_range_id") val shootingRangeId: Int,
    val name: String,
    @Embedded val address: Address,
    val comment: String
)

data class Address (
    val street: String,
    @ColumnInfo("house_number") val houseNumber: String,
    @ColumnInfo("postal_code") val postalCode: Int,
    val city: String
)