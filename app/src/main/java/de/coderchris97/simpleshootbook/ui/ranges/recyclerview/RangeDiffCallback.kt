package de.coderchris97.simpleshootbook.ui.ranges.recyclerview

import androidx.recyclerview.widget.DiffUtil
import de.coderchris97.simpleshootbook.database.entities.ShootingRange

class RangeDiffCallback: DiffUtil.ItemCallback<ShootingRange>() {
    override fun areItemsTheSame(oldItem: ShootingRange, newItem: ShootingRange): Boolean {
        return oldItem.shootingRangeId == newItem.shootingRangeId
    }

    override fun areContentsTheSame(oldItem: ShootingRange, newItem: ShootingRange): Boolean {
        return oldItem.name == newItem.name && oldItem.address == newItem.address
    }
}