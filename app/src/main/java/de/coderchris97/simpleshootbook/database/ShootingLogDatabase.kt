package de.coderchris97.simpleshootbook.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import de.coderchris97.simpleshootbook.database.entities.Hit
import de.coderchris97.simpleshootbook.database.entities.ShootingLogEntry
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.database.entities.Weapon

@Database(
    entities = [
        ShootingLogEntry::class,
        Weapon::class,
        ShootingRange::class,
        Hit::class
    ],
    version = 1
)
abstract class ShootingLogDatabase: RoomDatabase() {
    abstract fun shootingLogDao(): ShootingLogDao

    companion object {
        private lateinit var database: ShootingLogDatabase

        fun getDatabase(context: Context): ShootingLogDatabase {
            if (!::database.isInitialized)
                database = Room.databaseBuilder(context, ShootingLogDatabase::class.java, "ShootingLogDatabase").build()

            return database
        }
    }
}