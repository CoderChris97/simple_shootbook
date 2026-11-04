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
import de.coderchris97.simpleshootbook.database.entities.Address
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RangeEditUiState(
    val name: String = "",
    val street: String = "",
    val housenumber: String = "",
    val postalCode: Int? = null,
    val city: String = "",
    val comment: String = "",
    private val displayNameErrorEnabled: Boolean = false,
    private val saveAttempted: Boolean = false
) {
    private val hasDisplayName get() = name.isNotBlank()
    val showDisplayNameError get() = (displayNameErrorEnabled || saveAttempted) && !hasDisplayName
    val isValid: Boolean get() = hasDisplayName
}

class RangeEditViewModel(private val dao: ShootingLogDao, savedStateHandle: SavedStateHandle): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]!!.applicationContext
                val dao = ShootingLogDatabase.getDatabase(context).shootingLogDao()
                RangeEditViewModel(dao, createSavedStateHandle())
            }
        }
    }

    private val rangeId = savedStateHandle.get<Long>("rangeId") ?: -1L
    private val _uiState = MutableStateFlow(RangeEditUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<Event>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        if (rangeId != -1L) {
            viewModelScope.launch(Dispatchers.Default) {
                dao.getShootingRange(rangeId)
                    ?.let { range ->
                        _uiState.update {
                            RangeEditUiState(
                                range.name,
                                range.address.street,
                                range.address.houseNumber,
                                range.address.postalCode,
                                range.address.city,
                                range.comment
                            )
                        }
                    }
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { uiState ->
            uiState.copy(name = name, displayNameErrorEnabled = true)
        }
    }

    fun onStreetChanged(street: String) {
        _uiState.update { uiState ->
            uiState.copy(street = street)
        }
    }

    fun onHousenumberChanged(housenumber: String) {
        _uiState.update { uiState ->
            uiState.copy(housenumber = housenumber)
        }
    }

    fun onPostalCodeChanged(postalCode: Int?) {
        _uiState.update { uiState ->
            uiState.copy(postalCode = postalCode)
        }
    }

    fun onCityChanged(city: String) {
        _uiState.update { uiState ->
            uiState.copy(city = city)
        }
    }

    fun onCommentChanged(comment: String) {
        _uiState.update { uiState ->
            uiState.copy(comment = comment)
        }
    }

    fun onApplyClicked() {
        _uiState.update { uiState ->
            uiState.copy(saveAttempted = true)
        }

        uiState.value.run {
            if (!isValid)
                return

            val rangeId = if (rangeId == -1L)
                0L
            else
                rangeId
            val range = ShootingRange(
                rangeId,
                name,
                Address(street, housenumber, postalCode, city),
                comment
            )

            viewModelScope.launch(Dispatchers.Default) {
                if (dao.insertShootingRange(range) != -1L)
                    _eventChannel.send(Event.ItemStored(true))
                else
                    _eventChannel.send(Event.ItemStored(false))
            }
        }
    }
}