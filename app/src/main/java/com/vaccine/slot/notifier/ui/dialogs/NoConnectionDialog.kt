package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutNoConnectionDialogBinding
import com.vaccine.slot.notifier.other.Constants.EXTRA_MESSAGE_CONNECTION

class NoConnectionDialog : BottomSheetDialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(isDisconnected: Boolean) = NoConnectionDialog().apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_MESSAGE_CONNECTION, isDisconnected)
            }
        }
    }

    private lateinit var layoutNoConnectionDialog: LayoutNoConnectionDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutNoConnectionDialog = LayoutNoConnectionDialogBinding.inflate(inflater)
        return layoutNoConnectionDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isDisconnected: Boolean? = false
        arguments?.let {
            isDisconnected = it.getBoolean(EXTRA_MESSAGE_CONNECTION)
        }

        if (isDisconnected != true) dialog?.dismiss()
    }

    override fun onStart() {
        super.onStart()

        dialog?.setCancelable(false)

        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}