package de.coderchris97.simpleshootbook.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    "hits",
    foreignKeys = [
        ForeignKey(
            ShootingLogEntry::class,
            arrayOf("shooting_log_entry_id"),
            arrayOf("shooting_log_entry_id"),
            ForeignKey.CASCADE
        )
    ]
)
data class Hit(
    @PrimaryKey(true)
    @ColumnInfo("hit_id") val hitId: Int,
    @ColumnInfo("shooting_log_entry_id") val shootingLogEntryId: Int,
    @ColumnInfo("hit_number") val hitNumber: Int,
    val rings: Double
)
