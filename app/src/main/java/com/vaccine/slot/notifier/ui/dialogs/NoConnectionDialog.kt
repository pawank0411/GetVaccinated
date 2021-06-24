package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutNoConnectionDialogBinding

class NoConnectionDialog : BottomSheetDialogFragment() {

    private lateinit var layoutNoConnectionDialog: LayoutNoConnectionDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutNoConnectionDialog = LayoutNoConnectionDialogBinding.inflate(inflater)
        return layoutNoConnectionDialog.root
    }

    override fun onStart() {
        super.onStart()

        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}