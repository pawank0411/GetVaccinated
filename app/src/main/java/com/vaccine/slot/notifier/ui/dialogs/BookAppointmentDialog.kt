package com.vaccine.slot.notifier.ui.dialogs

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat.getColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.LayoutBookAppointmentDialogBinding
import com.vaccine.slot.notifier.other.Constants.APP_TAG
import com.vaccine.slot.notifier.other.Constants.CO_WIN_LINK
import com.vaccine.slot.notifier.other.Constants.EXTRA_TITLE_BOOK

class BookAppointmentDialog : BottomSheetDialogFragment() {
    companion object {

        @JvmStatic
        fun newInstance(title: String) = BookAppointmentDialog().apply {
            arguments = Bundle().apply {
                putString(EXTRA_TITLE_BOOK, title)
            }
        }
    }

    private lateinit var layoutBookAppointmentDialog: LayoutBookAppointmentDialogBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        layoutBookAppointmentDialog = LayoutBookAppointmentDialogBinding.inflate(inflater)
        return layoutBookAppointmentDialog.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var title: String? = APP_TAG

        arguments?.let {
            title = it.getString(EXTRA_TITLE_BOOK)
        }

        layoutBookAppointmentDialog.centerName.text = title

        layoutBookAppointmentDialog.close.setOnClickListener {
            dialog?.dismiss()
        }

        layoutBookAppointmentDialog.bookAppointment.setOnClickListener {
            openCoWinWebsite()
            dialog?.dismiss()
        }
    }

    private fun openCoWinWebsite() {
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(getColor(requireContext(), R.color.blue_700))
                .build()
        builder.setShowTitle(true)
        builder.setDefaultColorSchemeParams(params)
        builder.build()
                .launchUrl(requireContext(), Uri.parse(CO_WIN_LINK))
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}