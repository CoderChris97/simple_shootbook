package de.coderchris97.simpleshootbook.ui.weapons.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.coderchris97.simpleshootbook.database.ShootingLogDao
import de.coderchris97.simpleshootbook.database.ShootingLogDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class WeaponsViewModel(dao: ShootingLogDao): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]!!.applicationContext
                val dao = ShootingLogDatabase.getDatabase(context).shootingLogDao()
                WeaponsViewModel(dao)
            }
        }
    }

    val weaponsStateFlow = dao.getWeapons().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}