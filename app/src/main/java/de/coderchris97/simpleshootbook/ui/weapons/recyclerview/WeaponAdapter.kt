package de.coderchris97.simpleshootbook.ui.weapons.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.coderchris97.simpleshootbook.R
import de.coderchris97.simpleshootbook.database.entities.Weapon
import de.coderchris97.simpleshootbook.databinding.ItemWeaponBinding

class WeaponAdapter(private val onWeaponClick: (Weapon) -> Unit): ListAdapter<Weapon, WeaponAdapter.WeaponViewHolder>(WeaponDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).weaponId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeaponBinding.inflate(inflater, parent, false)
        return WeaponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        val weapon = getItem(position)
        holder.bind(weapon)
    }

    inner class WeaponViewHolder(val binding: ItemWeaponBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(weapon: Weapon) {
            val resources = binding.root.context.resources
            val displayName = if (weapon.manufacturer.isNotBlank() || weapon.model.isNotBlank())
                resources.getString(R.string.weapon_item_manufacturer_model, weapon.name, "${weapon.manufacturer} ${weapon.model}".trim())
            else
                weapon.name
            binding.displayNameTextView.text = displayName
            binding.serialNumberTextView.text = weapon.serialNumber
            binding.weaponTypeTextView.text = resources.getStringArray(R.array.weapon_type_display_name_short)[weapon.weaponType.ordinal]
            binding.calibreTextView.text = resources.getStringArray(R.array.calibres)[weapon.calibre.ordinal]
            binding.root.setOnClickListener {
                onWeaponClick(weapon)
            }
        }
    }
}