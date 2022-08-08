package com.goat.exosample.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder
import com.airbnb.epoxy.TypedEpoxyController
import com.goat.exosample.ExoAdapter
import com.goat.exosample.ExoAdapter.Companion
import com.goat.exosample.ExoPool
import timber.log.Timber

class EpoxyExoController(
  private val exoPool: ExoPool,
  private val onUpdatePlaybackPosition: (Int, Long) -> Unit
) : TypedEpoxyController<List<MediaData>>() {

  override fun buildModels(media: List<MediaData>) {
    media.forEachIndexed { index, m ->
      videoHeroView {
        id(index)
        player(this@EpoxyExoController.exoPool.acquire())
        media(m)
        updatePlaybackPositionListener { playbackPosition ->
          Timber.tag(Companion.VIDEO_LIST).d(
            "release player: position %d, playback position: %d",
            index,
            playbackPosition
          )
          this@EpoxyExoController.onUpdatePlaybackPosition(index, playbackPosition)
        }
      }
    }
  }

  override fun onViewAttachedToWindow(holder: EpoxyViewHolder, model: EpoxyModel<*>) {
    super.onViewAttachedToWindow(holder, model)
    when(val itemView = holder.itemView) {
      is VideoHeroView -> {
        Timber.tag(ExoAdapter.VIDEO_LIST).d("view visible again: position %d", holder.absoluteAdapterPosition)
        itemView.restore()
      }
    }
  }

  override fun onViewDetachedFromWindow(holder: EpoxyViewHolder, model: EpoxyModel<*>) {
    super.onViewDetachedFromWindow(holder, model)
    when(val itemView = holder.itemView) {
      is VideoHeroView -> {
        itemView.release()
      }
    }
  }
}