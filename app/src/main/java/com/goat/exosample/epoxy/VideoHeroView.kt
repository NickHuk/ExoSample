/*
package com.goat.exosample.epoxy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ALL
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT
import com.goat.exosample.Player
import com.goat.exosample.VIDEO_LIST
import com.goat.exosample.databinding.VideoHeroViewBinding
import timber.log.Timber

@ModelView(autoLayout = MATCH_WIDTH_WRAP_HEIGHT)
class VideoHeroView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {
  private lateinit var player: Player

  private val binding: VideoHeroViewBinding = VideoHeroViewBinding.inflate(
    LayoutInflater.from(context), this, true
  )

  private var updatePlaybackPositionListener: ((Long) -> Unit)? = null

  @CallbackProp
  fun setUpdatePlaybackPositionListener(listener: ((Long) -> Unit)?) {
    updatePlaybackPositionListener = listener
  }

  @ModelProp(options = [ModelProp.Option.DoNotHash])
  fun setPlayer(player: Player) {
    this.player = player
  }

  @ModelProp
  fun setMedia(media: MediaData) {
    player.setOnReleaseListener {
      updatePlaybackPositionListener?.invoke(player.exoPlayer.get()?.currentPosition ?: 0)
    }
    player.setOnRestartListener {
      bindPlayer(player, media.url, media.playbackPosition)
      player.setOnReleaseListener {
        updatePlaybackPositionListener?.invoke(player.exoPlayer.get()?.currentPosition ?: 0)
      }
      Timber.tag(VIDEO_LIST).d(
        "restart player: position ?, playbackPosition %d",
        media.playbackPosition
      )
    }
    bindPlayer(player, media.url, media.playbackPosition)
  }

  private fun bindPlayer(player: Player, url: String, playbackPosition: Long) {
    player.exoPlayer.get()?.run {
      binding.playerView.player = this
      setMediaItem(MediaItem.fromUri(url))
      repeatMode = REPEAT_MODE_ALL
      playWhenReady = true
      seekTo(0, playbackPosition)
      prepare()
    }
  }


}*/
