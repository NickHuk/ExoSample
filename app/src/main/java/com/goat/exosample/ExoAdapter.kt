package com.goat.exosample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.goat.exosample.ExoAdapter.ExoViewHolder
import com.goat.exosample.databinding.VideoHeroViewBinding
import timber.log.Timber
import java.lang.ref.WeakReference

class ExoAdapter(
  private val exoPool: ExoPool,
  private val updateMediaData: (Int, Long) -> Unit
) : RecyclerView.Adapter<ExoViewHolder>() {

  private var media: List<String> = listOf()
  var playbackPositions: List<Long> = listOf()

  fun updateMedia(media: List<String>) {
    this.media = media
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoViewHolder =
    ExoViewHolder(
      VideoHeroViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        .apply {
          playerView.hideController()
        },
      exoPool.acquire()
    )

  override fun onBindViewHolder(holder: ExoViewHolder, position: Int) {
    Timber.tag(VIDEO_LIST).d("onBindViewHolder: position %d", position)
    holder.bind(media[holder.absoluteAdapterPosition])
  }

  override fun getItemCount(): Int = media.size

  override fun onViewAttachedToWindow(holder: ExoViewHolder) {
    super.onViewAttachedToWindow(holder)
    holder.play()
  }

  override fun onViewDetachedFromWindow(holder: ExoViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.releasePlayer()
  }

  inner class ExoViewHolder(
    private val binging: VideoHeroViewBinding,
    private val player: Player
  ) : RecyclerView.ViewHolder(binging.root) {

    fun bind(url: String) {
      player.setOnReleaseListener {
        updateMediaData(absoluteAdapterPosition, player.exoPlayer.get()?.currentPosition ?: 0)
        Timber.tag(VIDEO_LIST).d(
          "release player: position %d, playback position: %d",
          absoluteAdapterPosition,
          player.exoPlayer.get()?.currentPosition ?: 0
        )
      }
      player.setOnRestartListener {
        bindPlayer(player, url, playbackPositions[absoluteAdapterPosition])
        player.setOnReleaseListener {
          updateMediaData(absoluteAdapterPosition, player.exoPlayer.get()?.currentPosition ?: 0)
          Timber.tag(VIDEO_LIST).d(
            "release player: position %d, playback position: %d",
            absoluteAdapterPosition,
            player.exoPlayer.get()?.currentPosition ?: 0
          )
        }
        Timber.tag(VIDEO_LIST).d(
          "restart player: position %d, playbackPosition %d",
          absoluteAdapterPosition,
          playbackPositions[absoluteAdapterPosition]
        )
      }
      bindPlayer(player, url, playbackPositions[absoluteAdapterPosition])
    }

    fun play() {
      Timber.tag(VIDEO_LIST).d("view visible again: position %d", absoluteAdapterPosition)
      if(player.exoPlayer.get() == null) {
        player.restart()
      }
    }

    fun releasePlayer() {
      player.releaseExoPlayer()
    }

    private fun bindPlayer(player: Player, url: String, playbackPosition: Long) {
      player.exoPlayer.get()?.run {
        binging.playerView.player = this
        setMediaItem(MediaItem.fromUri(url))
        playWhenReady = true
        seekTo(0, playbackPosition)
        prepare()
      }
    }
  }

  companion object {
    const val VIDEO_LIST = "VideoList"
  }
}