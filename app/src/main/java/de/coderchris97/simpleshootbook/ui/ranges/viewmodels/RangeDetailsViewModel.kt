package de.coderchris97.simpleshootbook.ui.ranges.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.coderchris97.simpleshootbook.database.ShootingLogDao
import de.coderchris97.simpleshootbook.database.ShootingLogDatabase
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RangeDetailsViewModel(private val dao: ShootingLogDao, savedStateHandle: SavedStateHandle): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]!!.applicationContext
                val dao = ShootingLogDatabase.getDatabase(context).shootingLogDao()
                RangeDetailsViewModel(dao, createSavedStateHandle())
            }
        }
    }

    private val rangeId = savedStateHandle.get<Long>("rangeId") ?: -1L
    val rangeStateFlow = dao.getShootingRangeAsFlow(rangeId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
    private val _eventChannel = Channel<Event>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onEditClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _eventChannel.send(Event.EditItem(rangeId))
        }
    }

    fun onDeleteClick() {
        val range = rangeStateFlow.value ?: return

        viewModelScope.launch(Dispatchers.Default) {
            val affectedEntries = dao.getEntriesByShootingRangeId(rangeId)
            _eventChannel.send(Event.ConfirmDeletion(range.name, affectedEntries.size))
        }
    }

    fun onDeleteConfirmed() {
        rangeStateFlow.value?.let {
            viewModelScope.launch(Dispatchers.Default) {
                dao.deleteShootingRange(it)
            }
        }
    }
}