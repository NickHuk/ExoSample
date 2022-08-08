package com.goat.exosample

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import java.lang.ref.WeakReference

class Player(private val context: Context) {
  private var _exoPlayer: ExoPlayer? = ExoPlayer.Builder(context).build()
  val exoPlayer: WeakReference<ExoPlayer>
    get() = WeakReference(_exoPlayer)
  private var onReleaseListener: OnReleasePlayerListener? = null
  private var onRestartListener: OnRestartListener? = null

  fun setOnRestartListener(listener: OnRestartListener) {
    onRestartListener = listener
  }

  fun setOnReleaseListener(listener: OnReleasePlayerListener) {
    onReleaseListener = listener
  }

  fun restart() {
    _exoPlayer = ExoPlayer.Builder(context)
      .build()
    onRestartListener?.onRestart()
  }

  fun releaseExoPlayer() {
    onReleaseListener?.onRelease()
    onReleaseListener = null
    _exoPlayer?.release()
    _exoPlayer = null
  }
}

fun interface OnReleasePlayerListener {
  fun onRelease()
}

fun interface OnRestartListener {
  fun onRestart()
}