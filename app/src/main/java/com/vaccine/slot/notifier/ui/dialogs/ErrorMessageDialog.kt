package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutErrorMessageDialogBinding
import com.vaccine.slot.notifier.other.Constants.DEFAULT_MESSAGE_ERROR
import com.vaccine.slot.notifier.other.Constants.EXTRA_MESSAGE_ERROR

class ErrorMessageDialog : BottomSheetDialogFragment() {
    companion object {

        @JvmStatic
        fun newInstance(title: String) = ErrorMessageDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_MESSAGE_ERROR, title)
            }
        }
    }

    private lateinit var layoutErrorMessageDialog: LayoutErrorMessageDialogBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        layoutErrorMessageDialog = LayoutErrorMessageDialogBinding.inflate(inflater)
        return layoutErrorMessageDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var message: String? = DEFAULT_MESSAGE_ERROR

        arguments?.let {
            message = it.getString(EXTRA_MESSAGE_ERROR)
        }

        layoutErrorMessageDialog.message.text = message

        layoutErrorMessageDialog.close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}