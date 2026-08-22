package de.coderchris97.simpleshootbook.ui.weapons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import de.coderchris97.simpleshootbook.R
import de.coderchris97.simpleshootbook.database.enums.Calibre
import de.coderchris97.simpleshootbook.database.enums.WeaponType
import de.coderchris97.simpleshootbook.databinding.FragmentWeaponEditBinding
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import de.coderchris97.simpleshootbook.ui.weapons.viewmodels.WeaponEditUiState
import de.coderchris97.simpleshootbook.ui.weapons.viewmodels.WeaponEditViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

class WeaponEditFragment: Fragment(), RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private var _binding: FragmentWeaponEditBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: WeaponEditViewModel by viewModels { WeaponEditViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeaponEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameEditText.doAfterTextChanged { mViewModel.onNameChanged(it.toString()) }
        binding.manufacturerEditText.doAfterTextChanged { mViewModel.onManufacturerChanged(it.toString()) }
        binding.modelEditText.doAfterTextChanged { mViewModel.onModelChanged(it.toString()) }
        binding.serialNumberEditText.doAfterTextChanged { mViewModel.onSerialNumberChanged(it.toString()) }
        binding.weaponTypeRadioGroup.setOnCheckedChangeListener(this)
        val calibreArray = resources.getStringArray(R.array.calibres)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, calibreArray)
        binding.calibreAutoCompleteTextView.setAdapter(adapter)
        binding.purchaseDateEditText.showSoftInputOnFocus = false
        binding.calibreAutoCompleteTextView.onItemClickListener = this
        binding.purchaseDateEditText.setOnClickListener(this)
        binding.purchaseDateEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                openDatePicker()
        }
        binding.purchaseDateEditText.doAfterTextChanged {
            if (it.isNullOrBlank())
                mViewModel.onPurchaseDateChanged(null)
        }
        binding.commentEditText.doAfterTextChanged { mViewModel.onCommentChanged(it.toString()) }
        binding.abortButton.setOnClickListener(this)
        binding.applyButton.setOnClickListener(this)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.uiState.collectLatest { uiState ->
                    Timber.d("uiState has changed: $uiState")
                    updateUi(uiState)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.eventChannel.collect { event ->
                    when (event) {
                        is Event.ItemStored -> {
                            val resId = if (event.successful) {
                                findNavController().popBackStack()
                                R.string.toast_store_item_successful
                            } else
                                R.string.toast_store_item_unsuccessful
                            Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
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

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (group != binding.weaponTypeRadioGroup)
            return

        val weaponType = when (checkedId) {
            binding.handgunRadioButton.id -> WeaponType.HANDGUN
            binding.rifleRadioButton.id -> WeaponType.RIFLE
            else -> null
        }
        mViewModel.onWeaponTypeChanged(weaponType)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val calibres = Calibre.entries
        val calibre = if (position in calibres.indices)
            calibres[position]
        else
            null
        mViewModel.onCalibreChanged(calibre)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.purchaseDateEditText -> openDatePicker()
            binding.abortButton -> findNavController().popBackStack()
            binding.applyButton -> mViewModel.onApplyClicked()
        }
    }

    private fun updateUi(uiState: WeaponEditUiState) {
        if (uiState.name != binding.nameEditText.text.toString())
            binding.nameEditText.setText(uiState.name)

        binding.nameEditText.error = if (uiState.showDisplayNameError)
            getString(R.string.error_display_name_missing)
        else
            null

        if (uiState.manufacturer != binding.manufacturerEditText.text.toString())
            binding.manufacturerEditText.setText(uiState.manufacturer)

        if (uiState.model != binding.modelEditText.text.toString())
            binding.modelEditText.setText(uiState.model)

        if (uiState.serialNumber != binding.serialNumberEditText.text.toString())
            binding.serialNumberEditText.setText(uiState.serialNumber)

        val checkId = when (uiState.weaponType) {
            WeaponType.HANDGUN -> binding.handgunRadioButton.id
            WeaponType.RIFLE -> binding.rifleRadioButton.id
            null -> -1
        }
        if (checkId != binding.weaponTypeRadioGroup.checkedRadioButtonId)
            binding.weaponTypeRadioGroup.check(checkId)

        binding.weaponTypeErrorTextView.isVisible = uiState.showWeaponTypeError

        val item = uiState.calibre?.ordinal?.let { binding.calibreAutoCompleteTextView.adapter.getItem(it) as? String }
        if (binding.calibreAutoCompleteTextView.text.toString() != item)
            binding.calibreAutoCompleteTextView.setText(item, false)

        binding.calibreAutoCompleteTextView.error = if (uiState.showCalibreError)
            getString(R.string.error_calibre_missing)
        else
            null

        val purchaseDateString = uiState.purchaseDate?.let { unixTimestamp ->
            val purchaseDate = Date(unixTimestamp)
            val dateFormatter = android.text.format.DateFormat.getDateFormat(context)
            dateFormatter.format(purchaseDate)
        }.orEmpty()
        if (binding.purchaseDateEditText.text.toString() != purchaseDateString)
            binding.purchaseDateEditText.setText(purchaseDateString)

        if (uiState.comment != binding.commentEditText.text.toString())
            binding.commentEditText.setText(uiState.comment)
    }

    private fun openDatePicker() {

        val datePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(R.string.select_purchase_date_dialog_title)
            setSelection(mViewModel.uiState.value.purchaseDate ?: MaterialDatePicker.todayInUtcMilliseconds())
        }.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            mViewModel.onPurchaseDateChanged(selection)
        }
        activity?.supportFragmentManager?.let { datePicker.show(it, null) }
    }

}