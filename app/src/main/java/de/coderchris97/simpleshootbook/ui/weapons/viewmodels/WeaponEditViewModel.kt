package de.coderchris97.simpleshootbook.ui.weapons.viewmodels

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
import de.coderchris97.simpleshootbook.database.entities.Weapon
import de.coderchris97.simpleshootbook.database.enums.Calibre
import de.coderchris97.simpleshootbook.database.enums.WeaponType
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeaponEditUiState(
    val name: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val serialNumber: String = "",
    val weaponType: WeaponType? = null,
    val calibre: Calibre? = null,
    val purchaseDate: Long? = null,
    val comment: String = "",
    private val displayNameErrorEnabled: Boolean = false,
    private val weaponTypeErrorEnabled: Boolean = false,
    private val calibreErrorEnabled: Boolean = false,
    private val saveAttempted: Boolean = false
) {
    private val hasDisplayName get() = name.isNotBlank()
    val showDisplayNameError get() = (displayNameErrorEnabled || saveAttempted) && !hasDisplayName
    private val hasWeaponType get() = weaponType != null
    val showWeaponTypeError get() = (weaponTypeErrorEnabled || saveAttempted) && !hasWeaponType
    private val hasCalibre get() = calibre != null
    val showCalibreError get() = (calibreErrorEnabled || saveAttempted) && !hasCalibre
    val isValid: Boolean get() = hasDisplayName && hasWeaponType && hasCalibre
}

class WeaponEditViewModel(private val dao: ShootingLogDao, savedStateHandle: SavedStateHandle): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[APPLICATION_KEY]!!.applicationContext
                val dao = ShootingLogDatabase.getDatabase(context).shootingLogDao()
                WeaponEditViewModel(dao, createSavedStateHandle())
            }
        }
    }

    private val weaponId = savedStateHandle.get<Long>("weaponId") ?: -1L
    private val _uiState = MutableStateFlow(WeaponEditUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<Event>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        if (weaponId != -1L) {
            viewModelScope.launch(Dispatchers.Default) {
                dao.getWeapon(weaponId)?.let { (_, name, manufacturer, model, serialNumber, weaponType, calibre, purchaseDate, comment) ->
                    _uiState.update {
                        WeaponEditUiState(name, manufacturer, model, serialNumber, weaponType, calibre, purchaseDate, comment)
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

    fun onManufacturerChanged(manufacturer: String) {
        _uiState.update { uiState ->
            uiState.copy(manufacturer = manufacturer)
        }
    }

    fun onModelChanged(model: String) {
        _uiState.update { uiState ->
            uiState.copy(model = model)
        }
    }

    fun onSerialNumberChanged(serialNumber: String) {
        _uiState.update { uiState ->
            uiState.copy(serialNumber = serialNumber)
        }
    }

    fun onWeaponTypeChanged(weaponType: WeaponType?) {
        _uiState.update { uiState ->
            uiState.copy(weaponType = weaponType, weaponTypeErrorEnabled = true)
        }
    }

    fun onCalibreChanged(calibre: Calibre?) {
        _uiState.update { uiState ->
            uiState.copy(calibre = calibre, calibreErrorEnabled = true)
        }
    }

    fun onPurchaseDateChanged(purchaseDate: Long?) {
        _uiState.update { uiState ->
            uiState.copy(purchaseDate = purchaseDate)
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

            val weaponId = if (weaponId == -1L)
                0L
            else
                weaponId
            val weapon = Weapon(weaponId, name, manufacturer, model, serialNumber, weaponType!!, calibre!!, purchaseDate, comment)

            viewModelScope.launch(Dispatchers.Default) {
            if (dao.insertWeapon(weapon) != -1L)
                _eventChannel.send(Event.ItemStored(true))
            else
                _eventChannel.send(Event.ItemStored(false))
            }
        }
    }

}