package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.vaccine.slot.notifier.databinding.LayoutCriteriaAlertDialogBinding
import com.vaccine.slot.notifier.other.Constants.MAP_VACCINE
import com.vaccine.slot.notifier.other.Constants.getKey

class CriteriaDialog : BottomSheetDialogFragment() {

    private var onClickListener: ((List<String>, List<String>) -> Unit)? = null
    private lateinit var layoutCriteriaAlertDialogBinding: LayoutCriteriaAlertDialogBinding

    fun setOnClickListener(listener: (List<String>, List<String>) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        layoutCriteriaAlertDialogBinding = LayoutCriteriaAlertDialogBinding.inflate(inflater)
        return layoutCriteriaAlertDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var chosenDose: List<String> = listOf("1")
        var chosenVaccine: List<String> = listOf("1", "2", "3")

        layoutCriteriaAlertDialogBinding.chipGroupDose.setOnCheckedChangeListener { _, checkedId: Int ->
            val chip: Chip? =
                    layoutCriteriaAlertDialogBinding.chipGroupDose.findViewById(checkedId) as Chip?
            chip?.let { chosenDose = listOf(it.text.toString().split(" ")[1]) }
        }

        layoutCriteriaAlertDialogBinding.chipGroupVaccine.setOnCheckedChangeListener { _, checkedId: Int ->
            val chip: Chip? =
                    layoutCriteriaAlertDialogBinding.chipGroupVaccine.findViewById(checkedId) as Chip?
            chip?.let {
                chosenVaccine = listOf(getKey(MAP_VACCINE, it.text.toString()))
            }
        }

        layoutCriteriaAlertDialogBinding.vaccineSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chosenVaccine = listOf("1")
                layoutCriteriaAlertDialogBinding.vaccine.visibility = VISIBLE
                layoutCriteriaAlertDialogBinding.chipGroupVaccine.visibility = VISIBLE
            } else {
                chosenVaccine = listOf("1", "2", "3")
                layoutCriteriaAlertDialogBinding.vaccine.visibility = INVISIBLE
                layoutCriteriaAlertDialogBinding.chipGroupVaccine.visibility = GONE
            }
        }

        layoutCriteriaAlertDialogBinding.close.setOnClickListener { dialog?.dismiss() }
        layoutCriteriaAlertDialogBinding.continueSubscribe.setOnClickListener {
            onClickListener?.let {
                it(chosenDose, chosenVaccine)
            }
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}