package de.coderchris97.simpleshootbook.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import de.coderchris97.simpleshootbook.database.enums.Calibre
import de.coderchris97.simpleshootbook.database.enums.WeaponType

@Entity("weapons")
data class Weapon(
    @PrimaryKey(true)
    @ColumnInfo("weapon_id") val weaponId: Int,
    @ColumnInfo("serial_number") val serialNumber: String,
    val manufacturer: String,
    val model: String,
    @ColumnInfo("weapon_type") val weaponType: WeaponType,
    val calibre: Calibre,
    @ColumnInfo("purchase_date") val purchaseDate: Long?,
    val comment: String
)
