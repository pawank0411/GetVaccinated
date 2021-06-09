package com.vaccine.slot.notifier.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.LayoutSubscribeNotificationDialogBinding
import com.vaccine.slot.notifier.other.Constants.EXTRA_MESSAGE_SUBSCRIBE
import com.vaccine.slot.notifier.other.Constants.EXTRA_TITLE_SUBSCRIBE

class SubscribeDialog : BottomSheetDialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(title: String, message: String) = SubscribeDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE_SUBSCRIBE, title)
                putString(EXTRA_MESSAGE_SUBSCRIBE, message)
            }
        }
    }

    private lateinit var layoutSubscribeNotificationDialogBinding: LayoutSubscribeNotificationDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutSubscribeNotificationDialogBinding = LayoutSubscribeNotificationDialogBinding.inflate(inflater)
        return layoutSubscribeNotificationDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var message: String? = ""
        var title: String? = resources.getString(R.string.notification_set)

        arguments?.let {
            title = it.getString(EXTRA_TITLE_SUBSCRIBE)
            message = it.getString(EXTRA_MESSAGE_SUBSCRIBE)
        }

        layoutSubscribeNotificationDialogBinding.title.text = title
        layoutSubscribeNotificationDialogBinding.description.text = message

        layoutSubscribeNotificationDialogBinding.close.setOnClickListener {
            dialog?.dismiss()
        }

        layoutSubscribeNotificationDialogBinding.shareApp.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "SAMPLE TEXT"
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}