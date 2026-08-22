package de.coderchris97.simpleshootbook.ui.interfaces

sealed interface Event {
    data class ItemStored(val successful: Boolean): Event
    data class EditItem(val itemId: Long): Event
    data class ConfirmDeletion(val displayName: String, val numberOfAffectedRows: Int): Event
}