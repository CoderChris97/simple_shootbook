package de.coderchris97.simpleshootbook.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(entry: ShootingLogEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeapon(weapon: Weapon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShootingRange(shootingRange: ShootingRange): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT * FROM shooting_log_entries WHERE weapon_id=:weaponId")
    fun getEntriesByWeaponId(weaponId: Long): List<ShootingLogEntry>

    @Query("SELECT * FROM shooting_log_entries WHERE shooting_range_id=:shootingRangeId")
    fun getEntriesByShootingRangeId(shootingRangeId: Long): List<ShootingLogEntry>

    @Query("SELECT * FROM weapons")
    fun getWeapons(): Flow<List<Weapon>>

    @Query("SELECT * FROM weapons WHERE weapon_id=:id")
    fun getWeapon(id: Long): Weapon?

    @Query("SELECT * FROM weapons WHERE weapon_id=:id")
    fun getWeaponAsFlow(id: Long): Flow<Weapon?>

    @Query("SELECT * FROM shooting_ranges")
    fun getShootingRanges(): Flow<List<ShootingRange>>

    @Query("SELECT * FROM shooting_ranges WHERE shooting_range_id=:id")
    fun getShootingRange(id: Long): ShootingRange?

    @Query("SELECT * FROM shooting_ranges WHERE shooting_range_id=:id")
    fun getShootingRangeAsFlow(id: Long): Flow<ShootingRange?>
}