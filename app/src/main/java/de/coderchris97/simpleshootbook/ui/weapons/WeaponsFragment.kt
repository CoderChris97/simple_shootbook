package de.coderchris97.simpleshootbook.ui.weapons

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
import de.coderchris97.simpleshootbook.database.entities.Weapon
import de.coderchris97.simpleshootbook.databinding.FragmentWeaponsBinding
import de.coderchris97.simpleshootbook.ui.weapons.recyclerview.WeaponAdapter
import de.coderchris97.simpleshootbook.ui.weapons.viewmodels.WeaponsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeaponsFragment: Fragment(), View.OnClickListener {
    private var _binding: FragmentWeaponsBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: WeaponsViewModel by viewModels { WeaponsViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeaponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = WeaponAdapter(::onWeaponClick)
        binding.weaponRecyclerView.adapter = adapter
        binding.weaponRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.addFloatingActionButton.setOnClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.weaponsStateFlow.collectLatest { weaponList ->
                    val numberOfWeaponsString = resources.getQuantityString(R.plurals.number_of_weapons, weaponList.size, weaponList.size)
                    binding.numberOfEntriesTextView.text = numberOfWeaponsString
                    adapter.submitList(weaponList)
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

        findNavController().navigate(WeaponsFragmentDirections.navigateOverviewToEdit())
    }

    fun onWeaponClick(weapon: Weapon) {
        findNavController().navigate(WeaponsFragmentDirections.navigateOverviewToDetails(weapon.weaponId))
    }
}