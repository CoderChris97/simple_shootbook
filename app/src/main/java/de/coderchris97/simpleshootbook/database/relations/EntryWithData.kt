package de.coderchris97.simpleshootbook.database.relations

import androidx.room3.Embedded
import androidx.room3.Relation
import de.coderchris97.simpleshootbook.database.entities.Hit
import de.coderchris97.simpleshootbook.database.entities.ShootingLogEntry
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.database.entities.Weapon

data class EntryWithData(
    @Embedded val shootingLogEntry: ShootingLogEntry,
    @Relation(
        parentColumns = ["weapon_id"],
        entityColumns = ["weapon_id"]
    )
    val weapon: Weapon,
    @Relation(
        parentColumns = ["shooting_log_entry_id"],
        entityColumns = ["shooting_log_entry_id"]
    )
    val result: List<Hit>,
    @Relation(
        parentColumns = ["shooting_range_id"],
        entityColumns = ["shooting_range_id"]
    )
    val shootingRange: ShootingRange
)
