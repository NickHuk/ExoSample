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

  override fun getDefaultLayout(): Int = R.layout.video_hero_view

  override fun createNewHolder(parent: ViewParent): VideoHeroHolder = VideoHeroHolder(
    exoPool,
    playbackManager
  )

  override fun bind(holder: VideoHeroHolder) {
    holder.bind(media, updatePlaybackPosition)
    Timber.tag(VIDEO_LIST).d("bind media: %s", media.url)
  }

  inner class VideoHeroHolder(
    private val exoPool: ExoPool,
    private val playbackManager: PlaybackManager
  ) : EpoxyHolder() {
    private lateinit var player: WeakReference<Player>
    lateinit var binding: VideoHeroViewBinding

    private lateinit var onRestartListener: OnRestartListener

    override fun bindView(itemView: View) {
      binding = VideoHeroViewBinding.bind(itemView)
    }

    fun bind(mediaData: MediaData, updatePlaybackPosition: ((Long) -> Unit)?) {
      player = exoPool.acquire()
      playbackManager.addOnRestartListener(
        HolderRestartListener(mediaData, updatePlaybackPosition).also {
          onRestartListener = it
        }
      )
      player.get()?.let { p ->
        Timber.tag(VIDEO_LIST).d("PLAY holder: %s video: %s player acquired: %s", hashCode(), mediaData.url.split('/').last(), player.get()?.hashCode())
        play(p, mediaData.url, mediaData.playbackPosition)
      }
    }

    fun restore(mediaData: MediaData, updatePlaybackPosition: ((Long) -> Unit)?) {
      if(player.get() === null) {
        Timber.tag(VIDEO_LIST).d("player restored url = %s playback position = %d", media.url.split('/').last(), media.playbackPosition)
        bind(mediaData, updatePlaybackPosition)
      }
    }

    fun stopPlayer(updatePlaybackPosition: ((Long) -> Unit)?) {
      playbackManager.detachListener(onRestartListener)
      player.get()?.let { p ->
        updatePlaybackPosition?.invoke(p.exoPlayer.currentPosition)
        exoPool.stop(p)
      }
      player.clear()
    }

    private fun play(player: Player, url: String, playbackPosition: Long) {
      player.exoPlayer.run {
        binding.playerView.player = this
        setMediaItem(MediaItem.fromUri(url))
        playWhenReady = true
        seekTo(0, playbackPosition)
        prepare()
      }
    }

    inner class HolderRestartListener(
      private val media: MediaData,
      private var updatePlaybackPosition: ((Long) -> Unit)?
    ) : OnRestartListener {
      private lateinit var player: WeakReference<Player>

      override fun onRelease() {
        player.get()?.exoPlayer?.let { exoPlayer ->
          Timber.tag(VIDEO_LIST).d("player released: video = %s", media.url.split('/').last())
          updatePlaybackPosition?.invoke(exoPlayer.currentPosition)
        }
      }

      override fun onRestart() {
        bind(media, updatePlaybackPosition)
      }
    }
  }
}