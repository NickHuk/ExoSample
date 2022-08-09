package com.goat.exosample.epoxy

import android.view.View
import android.view.ViewParent
import androidx.media3.common.MediaItem
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.goat.exosample.ExoPool
import com.goat.exosample.OnRestartListener
import com.goat.exosample.PlaybackManager
import com.goat.exosample.Player
import com.goat.exosample.R
import com.goat.exosample.VIDEO_LIST
import com.goat.exosample.databinding.VideoHeroViewBinding
import com.goat.exosample.epoxy.VideoHeroModel.VideoHeroHolder
import timber.log.Timber
import java.lang.ref.WeakReference

@EpoxyModelClass
abstract class VideoHeroModel(
  private val exoPool: ExoPool,
  private val playbackManager: PlaybackManager
) : EpoxyModelWithHolder<VideoHeroHolder>() {
  @EpoxyAttribute var media: MediaData = MediaData("")
  @EpoxyAttribute(DoNotHash) var updatePlaybackPosition: ((Long) -> Unit)? = null

  private lateinit var player: WeakReference<Player>
  private val onRestartListener: OnRestartListener by lazy {
    object : OnRestartListener {
      override fun onRelease() {

      }

      override fun onRestart() {
        bind(media)
      }
    }
  }

  override fun getDefaultLayout(): Int = R.layout.video_hero_view

  override fun createNewHolder(parent: ViewParent): VideoHeroHolder = VideoHeroHolder(
    exoPool,
    playbackManager
  )

  override fun bind(holder: VideoHeroHolder) {
    player = exoPool.acquire()
    playbackManager.addOnRestartListener(onRestartListener)
    player.get()?.let { p ->
      Timber.tag(VIDEO_LIST).d("PLAY holder: %s video: %s player acquired: %s", hashCode(), media.url.split('/').last(), player.get()?.hashCode())
      play(holder, p, media.url, media.playbackPosition)
    }
  }

  private fun play(holder: VideoHeroHolder, player: Player, url: String, playbackPosition: Long) {
    player.exoPlayer.run {
      holder.binding.playerView.player = this
      setMediaItem(MediaItem.fromUri(url))
      playWhenReady = true
      seekTo(0, playbackPosition)
      prepare()
    }
  }

  fun restore(holder: VideoHeroHolder) {
    if(player.get() === null) {
      Timber.tag(VIDEO_LIST).d("player restored url = %s playback position = %d", media.url.split('/').last(), media.playbackPosition)
      player = exoPool.acquire()
      playbackManager.addOnRestartListener(onRestartListener)
      player.get()?.let { p ->
        Timber.tag(VIDEO_LIST).d("PLAY holder: %s video: %s player acquired: %s", hashCode(), media.url.split('/').last(), player.get()?.hashCode())
        play(holder, p, media.url, media.playbackPosition)
      }
    }
  }

  fun stopPlayer() {
    playbackManager.detachListener(onRestartListener)
    player.get()?.let { p ->
      updatePlaybackPosition?.invoke(p.exoPlayer.currentPosition)
      exoPool.stop(p)
    }
    player.clear()
  }

  inner class VideoHeroHolder : EpoxyHolder() {
    lateinit var binding: VideoHeroViewBinding

    override fun bindView(itemView: View) {
      binding = VideoHeroViewBinding.bind(itemView)
    }
  }

  inner class ModelRestartListener : OnRestartListener {
    override fun onRelease() {
      player.get()?.exoPlayer?.let { exoPlayer ->
        Timber.tag(VIDEO_LIST).d("player released: video = %s", media.url.split('/').last())
        updatePlaybackPosition?.invoke(exoPlayer.currentPosition)
      }
    }

    override fun onRestart() {
      TODO("Not yet implemented")
    }
  }
}