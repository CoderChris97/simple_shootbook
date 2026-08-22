package de.coderchris97.simpleshootbook.ui.weapons

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
import de.coderchris97.simpleshootbook.database.entities.Weapon
import de.coderchris97.simpleshootbook.databinding.FragmentWeaponDetailsBinding
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import de.coderchris97.simpleshootbook.ui.weapons.viewmodels.WeaponDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class WeaponDetailsFragment: Fragment() {

    private var _binding: FragmentWeaponDetailsBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: WeaponDetailsViewModel by viewModels { WeaponDetailsViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeaponDetailsBinding.inflate(inflater, container, false)
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
                mViewModel.weaponStateFlow.collectLatest { weapon ->
                    updateUI(weapon)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.eventFlow.collect { event ->
                    when (event) {
                        is Event.EditItem -> {
                            findNavController().navigate(WeaponDetailsFragmentDirections.navigateDetailsToEdit(event.itemId))
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

    private fun updateUI(weapon: Weapon?) {
        binding.titleTextView.text = weapon?.name.orEmpty().ifBlank { "-" }
        binding.manufacturerTextView.text = weapon?.manufacturer.orEmpty().ifBlank { "-" }
        binding.modelTextView.text = weapon?.model.orEmpty().ifBlank { "-" }
        binding.serialNumberTextView.text = weapon?.serialNumber.orEmpty().ifBlank { "-" }
        binding.weaponTypeTextView.text = weapon?.weaponType?.ordinal?.let {
            resources.getStringArray(R.array.weapon_type_display_name)[it]
        }.orEmpty().ifBlank { "-" }
        binding.calibreTextView.text = weapon?.calibre?.ordinal?.let {
            resources.getStringArray(R.array.calibres)[it]
        }.orEmpty().ifBlank { "-" }
        binding.purchaseDateTextView.text = weapon?.purchaseDate?.let { unixTimestamp ->
            val purchaseDate = Date(unixTimestamp)
            val dateFormatter = android.text.format.DateFormat.getDateFormat(context)
            dateFormatter.format(purchaseDate)
        }.orEmpty().ifBlank { "-" }
        binding.commentTextView.text = weapon?.comment.orEmpty().ifBlank { "-" }
    }
}