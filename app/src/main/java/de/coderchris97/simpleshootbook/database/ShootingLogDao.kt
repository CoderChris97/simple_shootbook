package de.coderchris97.simpleshootbook.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import de.coderchris97.simpleshootbook.database.entities.Hit
import de.coderchris97.simpleshootbook.database.entities.ShootingLogEntry
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.database.entities.Weapon
import de.coderchris97.simpleshootbook.database.relations.EntryWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface ShootingLogDao {

    // ===== Inserts =====

    @Insert
    fun insertEntry(entry: ShootingLogEntry): Long

    @Insert
    fun insertWeapon(weapon: Weapon): Long

    @Insert
    fun insertShootingRange(shootingRange: ShootingRange): Long

    @Insert
    fun insertHits(vararg hits: Hit)

    // ===== Deletes =====

    @Delete
    fun deleteEntry(entry: ShootingLogEntry)

    @Delete
    fun deleteWeapon(weapon: Weapon)

    @Delete
    fun deleteShootingRange(shootingRange: ShootingRange)

    @Delete
    fun deleteHits(vararg hits: Hit)

    // ===== Queries =====

    @Transaction
    @Query("SELECT * FROM shooting_log_entries")
    fun getEntries(): Flow<List<EntryWithData>>

    @Query("Select * FROM shooting_log_entries WHERE shooting_log_entry_id=:id")
    fun getEntryById(id: Long): ShootingLogEntry?

    @Query("SELECT * FROM weapons")
    fun getWeapons(): Flow<List<Weapon>>

    @Query("SELECT * FROM shooting_ranges")
    fun getShootingRanges(): Flow<List<ShootingRange>>
}