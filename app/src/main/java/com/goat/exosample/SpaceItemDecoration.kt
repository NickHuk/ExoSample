package com.goat.exosample

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.State

class SpaceItemDecoration : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
    super.getItemOffsets(outRect, view, parent, state)
    outRect.bottom = outRect.bottom + 1024
  }
}