package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutUnsubscribeDialogBinding
import com.vaccine.slot.notifier.other.Constants.EXTRA_MESSAGE_UNSUBSCRIBE

class UnsubscribeDialog : BottomSheetDialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(message: String) = UnsubscribeDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_MESSAGE_UNSUBSCRIBE, message)
            }
        }
    }

    private lateinit var layoutUnsubscribeDialog: LayoutUnsubscribeDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutUnsubscribeDialog = LayoutUnsubscribeDialogBinding.inflate(inflater)
        return layoutUnsubscribeDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var message: String? = ""

        arguments?.let {
            message = it.getString(EXTRA_MESSAGE_UNSUBSCRIBE)
        }

        layoutUnsubscribeDialog.message.text = message

        layoutUnsubscribeDialog.close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}