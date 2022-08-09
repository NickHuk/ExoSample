package com.goat.exosample

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

class Player(context: Context) {
  private var _exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
  val exoPlayer: ExoPlayer = _exoPlayer
  var used: Int = 0


  fun releaseExoPlayer() {
    _exoPlayer.release()
  }

  fun stopExoPlayer() {
    used ++
    _exoPlayer.playWhenReady = false
    _exoPlayer.stop()
  }
}