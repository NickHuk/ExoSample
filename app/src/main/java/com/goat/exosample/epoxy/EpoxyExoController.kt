package com.goat.exosample.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyViewHolder
import com.airbnb.epoxy.TypedEpoxyController
import com.goat.exosample.ExoPool
import com.goat.exosample.PlaybackManager
import com.goat.exosample.VIDEO_LIST
import timber.log.Timber

class EpoxyExoController(
  private val exoPool: ExoPool,
  private val playbackManager: PlaybackManager,
  private val onUpdatePlaybackPosition: (Int, Long) -> Unit
) : TypedEpoxyController<List<MediaData>>() {

  override fun buildModels(media: List<MediaData>) {
    media.forEachIndexed { index, m ->
      videoHero(exoPool, playbackManager) {
        id(index)
        media(m)
        updatePlaybackPosition { playbackPosition ->
          this@EpoxyExoController.onUpdatePlaybackPosition(index, playbackPosition)
        }
      }
    }
  }

  override fun onViewAttachedToWindow(epoxyViewHolder: EpoxyViewHolder, model: EpoxyModel<*>) {
    super.onViewAttachedToWindow(epoxyViewHolder, model)
    when(val holder = epoxyViewHolder.holder) {
      is VideoHeroModel.VideoHeroHolder -> {
        holder.restore((model as VideoHeroModel).media) { playbackPosition ->
          onUpdatePlaybackPosition(epoxyViewHolder.absoluteAdapterPosition, playbackPosition)
        }
      }
    }
  }

  override fun onViewDetachedFromWindow(epoxyViewHolder: EpoxyViewHolder, model: EpoxyModel<*>) {
    super.onViewDetachedFromWindow(epoxyViewHolder, model)
    when(val holder = epoxyViewHolder.holder) {
      is VideoHeroModel.VideoHeroHolder -> {
        holder.stopPlayer { playbackPosition ->
          onUpdatePlaybackPosition(epoxyViewHolder.absoluteAdapterPosition, playbackPosition)
        }
      }
    }
  }
}