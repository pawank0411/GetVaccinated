package com.vaccine.slot.notifier.other

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class GridTab(context: Context) : Carousel(context) {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, SPAN_COUNT, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        private const val SPAN_COUNT = 2
    }
}