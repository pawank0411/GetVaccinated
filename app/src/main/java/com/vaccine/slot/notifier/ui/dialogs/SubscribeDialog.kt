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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutSubscribeNotificationDialogBinding =
            LayoutSubscribeNotificationDialogBinding.inflate(inflater)
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
                "Are you vaccinated?\n" +
                        "\n" +
                        "Have you booked your slots for vaccination yet?\n" +
                        "\n" +
                        "If not then install \"Get Vaccinated\" app asap. Find suitable slots and get notified at the right moment when slots open at your preferred area.\n" +
                        "Be strong headed , Get Vaccinated..\n" +
                        "\n" +
                        "Link: https://bit.ly/3y8SWpn\n" +
                        "\n" +
                        "For any issues or features request feel free to contact us at \n" +
                        "getvaccinated.sup@gmail.com\n" +
                        "\n" +
                        "To get proper knowledge about the app watch this.\n" +
                        "\n" +
                        "https://youtu.be/qnMJ-LEpY70\n" +
                        "\n" +
                        "Stay safe, Get Vaccinated ✌\uD83C\uDFFB"
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