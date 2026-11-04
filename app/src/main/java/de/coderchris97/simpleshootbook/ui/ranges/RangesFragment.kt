package de.coderchris97.simpleshootbook.ui.ranges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.coderchris97.simpleshootbook.R
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.databinding.FragmentRangesBinding
import de.coderchris97.simpleshootbook.ui.ranges.recyclerview.RangeAdapter
import de.coderchris97.simpleshootbook.ui.ranges.viewmodels.RangesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RangesFragment: Fragment(), View.OnClickListener {
    private var _binding: FragmentRangesBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: RangesViewModel by viewModels { RangesViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRangesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RangeAdapter(::onRangeClick)
        binding.rangesRecyclerView.adapter = adapter
        binding.rangesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.addFloatingActionButton.setOnClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.rangesStateFlow.collectLatest { rangesList ->
                    val numberOfWeaponsString = resources.getQuantityString(R.plurals.number_of_shooting_ranges, rangesList.size, rangesList.size)
                    binding.numberOfEntriesTextView.text = numberOfWeaponsString
                    adapter.submitList(rangesList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onClick(v: View?) {
        if (v != binding.addFloatingActionButton)
            return

        findNavController().navigate(RangesFragmentDirections.navigateOverviewToEdit())
    }

    fun onRangeClick(range: ShootingRange) {
        findNavController().navigate(RangesFragmentDirections.navigateOverviewToDetails(range.shootingRangeId))
    }
}