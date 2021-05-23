package com.vaccine.slot.notifier.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vaccine.slot.notifier.ItemLayoutBottomSheetBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.BottomSheetLayoutBinding
import com.vaccine.slot.notifier.databinding.FragmentDistrictBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchByDistrictFragment : Fragment() {

    private var _binding: FragmentDistrictBinding? = null
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var searchByDistrictViewModel: SearchByDistrictViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        exitTransition = android.transition.Fade()
        reenterTransition = android.transition.Fade()
        if (_binding == null)
            _binding = FragmentDistrictBinding.inflate(layoutInflater)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchByDistrictViewModel = ViewModelProvider(this).get(SearchByDistrictViewModel::class.java)
        searchByDistrictViewModel.getStateList()

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)
        bottomSheetLayoutBinding.dataList.layoutManager = LinearLayoutManager(requireContext())

        // pre-populate state list
        populateStateList()

        binding.stateNameEditText.setOnClickListener {
            populateStateList()
            bottomSheetLayoutBinding.bottomSheetTitle.text =
                    resources.getString(R.string.select_your_state)
            bottomSheetDialog.show()
        }

        binding.districtEditText.setOnClickListener {
            populateStateDistrictList()
            bottomSheetLayoutBinding.bottomSheetTitle.text =
                    resources.getString(R.string.select_your_district)
            bottomSheetDialog.show()
        }

        searchByDistrictViewModel.stateList.observe(viewLifecycleOwner, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })

        searchByDistrictViewModel.districtList.observe(viewLifecycleOwner, {
            bottomSheetLayoutBinding.dataList.requestModelBuild()
        })
    }

    private fun populateStateList() {
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {

                val dataList = searchByDistrictViewModel.stateList.value
                dataList?.forEach { state ->
                    ItemLayoutBottomSheetBindingModel_()
                            .id(state.stateId)
                            .name(state.stateName)
                            .onClick { _ ->
                                selectedStateName = state.stateName
                                binding.stateNameEditText.setText(state.stateName)
                                searchByDistrictViewModel.getDistrictList(state.stateId) // pre-fetch all districts of state
                                bottomSheetDialog.dismiss()
                            }
                            .addTo(controller)
                } ?: bottomSheetDialog.dismiss()
            }
        })
    }

    private fun populateStateDistrictList() {
        bottomSheetLayoutBinding.dataList.buildModelsWith(object :
                EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = searchByDistrictViewModel.districtList.value
                dataList?.forEach { district ->
                    ItemLayoutBottomSheetBindingModel_()
                            .id(district.districtId)
                            .name(district.districtName)
                            .onClick { _ ->
                                selectedDistrictName = district.districtName
                                selectedPinCodeId = district.districtId
                                binding.districtEditText.setText(district.districtName)
                                bottomSheetDialog.dismiss()
                            }
                            .addTo(controller)
                } ?: bottomSheetDialog.dismiss()
            }
        })
    }

    companion object {
        var selectedStateName: String = String()
        var selectedDistrictName: String = String()
        var selectedPinCodeId: Int = 0
    }
}