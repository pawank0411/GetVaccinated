package com.vaccine.slot.notifier.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vaccine.slot.notifier.ItemLayoutBottomSheetBindingModel_
import com.vaccine.slot.notifier.R
import com.vaccine.slot.notifier.databinding.LayoutStatedistrictListBinding
import com.vaccine.slot.notifier.ui.home.HomeViewModel

class DistrictDialog : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "DistrictDialog"
    }

    private var onClickListener: ((String, Int) -> Unit)? = null

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var layoutStateDistrictListBinding: LayoutStatedistrictListBinding

    fun setOnClickListener(listener: (String, Int) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutStateDistrictListBinding = LayoutStatedistrictListBinding.inflate(inflater)
        return layoutStateDistrictListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        layoutStateDistrictListBinding.bottomSheetTitle.text = resources.getString(R.string.select_your_district)
        layoutStateDistrictListBinding.dataList.layoutManager = LinearLayoutManager(requireContext())
        layoutStateDistrictListBinding.dataList.buildModelsWith(object : EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = homeViewModel.districtList.value
                dataList?.forEach { district ->
                    ItemLayoutBottomSheetBindingModel_()
                            .id(district.districtId)
                            .name(district.districtName)
                            .onClick { _ ->
                                onClickListener?.let { selected ->
                                    selected(district.districtName, district.districtId)
                                }
                                dialog?.dismiss()
                            }
                            .addTo(controller)
                } ?: dialog?.dismiss()
            }

        })

        homeViewModel.districtList.observe(this, {
            layoutStateDistrictListBinding.dataList.requestModelBuild()
        })
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}