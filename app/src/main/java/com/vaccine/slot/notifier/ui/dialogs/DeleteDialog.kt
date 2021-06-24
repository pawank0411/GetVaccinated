package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.databinding.LayoutDeleteDialogBinding

class DeleteDialog : BottomSheetDialogFragment() {

    private var onClickListener: ((String) -> Unit)? = null
    private lateinit var deleteDialog: LayoutDeleteDialogBinding

    fun setOnClickListener(listener: (String) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        deleteDialog = LayoutDeleteDialogBinding.inflate(inflater)
        return deleteDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteDialog.delete.setOnClickListener {
            onClickListener?.let { it1 -> it1("clicked") }
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}