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
import com.vaccine.slot.notifier.databinding.LayoutStatedistrictDialogBinding
import com.vaccine.slot.notifier.ui.home.HomeViewModel

class StateDialog : BottomSheetDialogFragment() {

    private var onClickListener: ((String, Int) -> Unit)? = null

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var layoutStateDistrictListBinding: LayoutStatedistrictDialogBinding

    fun setOnClickListener(listener: (String, Int) -> Unit) {
        onClickListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutStateDistrictListBinding = LayoutStatedistrictDialogBinding.inflate(inflater)
        return layoutStateDistrictListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        layoutStateDistrictListBinding.bottomSheetTitle.text = resources.getString(R.string.select_your_state)
        layoutStateDistrictListBinding.dataList.layoutManager = LinearLayoutManager(requireContext())
        layoutStateDistrictListBinding.dataList.buildModelsWith(object :
            EpoxyRecyclerView.ModelBuilderCallback {
            override fun buildModels(controller: EpoxyController) {
                val dataList = homeViewModel.stateList.value
                dataList?.forEach { state ->
                    ItemLayoutBottomSheetBindingModel_()
                        .id(state.stateId)
                        .name(state.stateName)
                        .onClick { _ ->
                            onClickListener?.let { selected ->
                                selected(state.stateName, state.stateId)
                            }
                            dialog?.dismiss()
                        }
                        .addTo(controller)
                } ?: dialog?.dismiss()
            }

        })

        homeViewModel.stateList.observe(this, {
            layoutStateDistrictListBinding.dataList.requestModelBuild()
        })
    }

    override fun onStart() {
        super.onStart()
        val behaviour = BottomSheetBehavior.from(requireView().parent as View)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
}