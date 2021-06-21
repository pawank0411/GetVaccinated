package com.vaccine.slot.notifier.ui.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutDialogSnackbarBinding
import com.vaccine.slot.notifier.other.Constants.ACTION_NOTIFICATION
import com.vaccine.slot.notifier.other.Constants.EXTRA_ACTION_SNACK
import com.vaccine.slot.notifier.other.Constants.EXTRA_MESSAGE_SNACK

class BottomSnackBarDialog : BottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(message: String, action: String) = BottomSnackBarDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_MESSAGE_SNACK, message)
                putString(EXTRA_ACTION_SNACK, action)
            }
        }
    }

    private var onClickListener: ((String) -> Unit)? = null
    private lateinit var bottomSnackBarDialog: LayoutDialogSnackbarBinding

    fun setOnClickListener(listener: (String) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bottomSnackBarDialog = LayoutDialogSnackbarBinding.inflate(inflater)
        return bottomSnackBarDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var message: String? = ""
        var action: String? = ""

        arguments?.let {
            message = it.getString(EXTRA_MESSAGE_SNACK)
            action = it.getString(EXTRA_ACTION_SNACK)
        }

        if (!message.isNullOrEmpty() && !action.isNullOrEmpty()) {
            bottomSnackBarDialog.message.text = message
            bottomSnackBarDialog.button.text = action
            if (action.equals(ACTION_NOTIFICATION))
                bottomSnackBarDialog.button.setTextColor(Color.RED)
            else
                bottomSnackBarDialog.button.setTextColor(Color.YELLOW)

            bottomSnackBarDialog.button.setOnClickListener {
                onClickListener?.let {
                    it(action.toString())
                }
                dialog?.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}