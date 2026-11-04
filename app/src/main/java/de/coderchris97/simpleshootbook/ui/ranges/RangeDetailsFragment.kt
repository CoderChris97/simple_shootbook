package de.coderchris97.simpleshootbook.ui.ranges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.coderchris97.simpleshootbook.R
import de.coderchris97.simpleshootbook.database.entities.ShootingRange
import de.coderchris97.simpleshootbook.databinding.FragmentRangeDetailsBinding
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import de.coderchris97.simpleshootbook.ui.ranges.viewmodels.RangeDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RangeDetailsFragment: Fragment() {

    private var _binding: FragmentRangeDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: RangeDetailsViewModel by viewModels { RangeDetailsViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRangeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_edit)?.apply {
                    isVisible = true
                }
                menu.findItem(R.id.action_delete)?.apply {
                    isVisible = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        mViewModel.onEditClick()
                        true
                    }
                    R.id.action_delete -> {
                        mViewModel.onDeleteClick()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.rangeStateFlow.collectLatest { range ->
                    updateUI(range)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.eventFlow.collect { event ->
                    when (event) {
                        is Event.EditItem -> {
                            findNavController().navigate(RangeDetailsFragmentDirections.navigateDetailsToEdit(event.itemId))
                        }
                        is Event.ConfirmDeletion -> {
                            MaterialAlertDialogBuilder(requireContext()).apply {
                                val title = getString(R.string.dialog_confirm_deletion_title, event.displayName)
                                setTitle(title)
                                val message = if (event.numberOfAffectedRows > 0)
                                    getString(R.string.dialog_confirm_deletion_cascade_message,event.displayName, event.numberOfAffectedRows)
                                else
                                    getString(R.string.dialog_confirm_deletion_message, event.displayName)
                                setMessage(message)
                                setPositiveButton(R.string.confirm) { dialogInterface, _ ->
                                    mViewModel.onDeleteConfirmed()
                                    findNavController().popBackStack()
                                    dialogInterface.dismiss()
                                }
                                setNegativeButton(R.string.abort) {dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                            }.show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun updateUI(range: ShootingRange?) {
        binding.titleTextView.text = range?.name.orEmpty().ifBlank { "-" }
        binding.streetTextView.text = range?.address?.street.orEmpty().ifBlank { "-" }
        binding.housenumberTextView.text = range?.address?.houseNumber.orEmpty().ifBlank { "-" }
        binding.postalCodeTextView.text = range?.address?.postalCode?.toString() ?: "-"
        binding.cityTextView.text = range?.address?.city.orEmpty().ifBlank { "-" }
        binding.commentTextView.text = range?.comment.orEmpty().ifBlank { "-" }
    }
}