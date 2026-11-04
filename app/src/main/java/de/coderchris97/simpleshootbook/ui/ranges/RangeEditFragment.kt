package de.coderchris97.simpleshootbook.ui.ranges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import de.coderchris97.simpleshootbook.R
import de.coderchris97.simpleshootbook.databinding.FragmentRangeEditBinding
import de.coderchris97.simpleshootbook.ui.interfaces.Event
import de.coderchris97.simpleshootbook.ui.ranges.viewmodels.RangeEditUiState
import de.coderchris97.simpleshootbook.ui.ranges.viewmodels.RangeEditViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class RangeEditFragment: Fragment(), View.OnClickListener {

    private var _binding: FragmentRangeEditBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: RangeEditViewModel by viewModels { RangeEditViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRangeEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameEditText.doAfterTextChanged { mViewModel.onNameChanged(it.toString()) }
        binding.streetEditText.doAfterTextChanged { mViewModel.onStreetChanged(it.toString()) }
        binding.housenumberEditText.doAfterTextChanged { mViewModel.onHousenumberChanged(it.toString()) }
        binding.postalCodeEditText.doAfterTextChanged {
            val postalCode = try {
                it.toString().toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            mViewModel.onPostalCodeChanged(postalCode)
        }
        binding.cityEditText.doAfterTextChanged { mViewModel.onCityChanged(it.toString()) }
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

    override fun onClick(v: View?) {
        when (v) {
            binding.abortButton -> findNavController().popBackStack()
            binding.applyButton -> mViewModel.onApplyClicked()
        }
    }

    private fun updateUi(uiState: RangeEditUiState) {
        if (uiState.name != binding.nameEditText.text.toString())
            binding.nameEditText.setText(uiState.name)

        binding.nameEditText.error = if (uiState.showDisplayNameError)
            getString(R.string.error_display_name_missing)
        else
            null

        if (uiState.street != binding.streetEditText.text.toString())
            binding.streetEditText.setText(uiState.street)

        if (uiState.housenumber != binding.housenumberEditText.text.toString())
            binding.housenumberEditText.setText(uiState.housenumber)

        val postalCodeString = uiState.postalCode?.toString() ?: ""
        if (postalCodeString != binding.postalCodeEditText.text.toString())
            binding.postalCodeEditText.setText(postalCodeString)

        if (uiState.city != binding.cityEditText.text.toString())
            binding.cityEditText.setText(uiState.city)

        if (uiState.comment != binding.commentEditText.text.toString())
            binding.commentEditText.setText(uiState.comment)
    }
}