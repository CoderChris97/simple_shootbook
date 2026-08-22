package de.coderchris97.simpleshootbook.ui.weapons.recyclerview

import androidx.recyclerview.widget.DiffUtil
import de.coderchris97.simpleshootbook.database.entities.Weapon

class WeaponDiffCallback: DiffUtil.ItemCallback<Weapon>() {
    override fun areItemsTheSame(oldItem: Weapon, newItem: Weapon): Boolean {
        return oldItem.weaponId == newItem.weaponId
    }

    override fun areContentsTheSame(oldItem: Weapon, newItem: Weapon): Boolean {
        return oldItem == newItem
    }

}