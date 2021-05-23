package com.vaccine.slot.notifier.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vaccine.slot.notifier.databinding.FragmentPinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchByPinFragment : Fragment() {

    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        exitTransition = android.transition.Fade()
        reenterTransition = android.transition.Fade()
        if (_binding == null)
            _binding = FragmentPinBinding.inflate(layoutInflater)

        return _binding!!.root
    }

    companion object {
        var selectedPinCode: String = String()
    }
}