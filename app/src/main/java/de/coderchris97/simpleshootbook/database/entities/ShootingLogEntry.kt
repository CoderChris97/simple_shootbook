package de.coderchris97.simpleshootbook.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import de.coderchris97.simpleshootbook.database.enums.Discipline

@Entity(
    "shooting_log_entries",
    foreignKeys = [
        ForeignKey(
            Weapon::class,
            arrayOf("weapon_id"),
            arrayOf("weapon_id"),
            ForeignKey.RESTRICT
        ),
        ForeignKey(
            ShootingRange::class,
            arrayOf("shooting_range_id"),
            arrayOf("shooting_range_id"),
            ForeignKey.RESTRICT
        )
    ]
)
data class ShootingLogEntry(
    @PrimaryKey(true)
    @ColumnInfo("shooting_log_entry_id") val shootingLogEntryId: Int,
    val timestamp: Long,
    @ColumnInfo("weapon_id") val weaponId: Int,
    val discipline: Discipline,
    @ColumnInfo("shooting_range_id") val shootingRangeId: Int,
    val comment: String
)
