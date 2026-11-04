package de.coderchris97.simpleshootbook.ui.ranges.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.databinding.ItemRangeBinding

class RangeAdapter(private val onRangeClick: (ShootingRange) -> Unit): ListAdapter<ShootingRange, RangeAdapter.RangeViewHolder>(RangeDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).shootingRangeId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RangeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRangeBinding.inflate(inflater, parent, false)
        return RangeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RangeViewHolder, position: Int) {
        val range = getItem(position)
        holder.bind(range)
    }

    inner class RangeViewHolder(val binding: ItemRangeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(range: ShootingRange) {
            binding.displayNameTextView.text = range.name
            val addressString = StringBuilder().apply {
                if (range.address.street.isNotBlank()) {
                    append(range.address.street)

                    if (range.address.houseNumber.isNotBlank())
                        append(" ${range.address.houseNumber}")
                }

                if (isNotBlank() && range.address.city.isNotBlank())
                    append(", ")

                if (range.address.city.isNotBlank()) {
                    if (range.address.postalCode != null)
                        append("${range.address.postalCode} ")

                    append(range.address.city)
                }
            }.toString()
            binding.addressTextView.text = addressString
            binding.root.setOnClickListener {
                onRangeClick(range)
            }
        }
    }
}