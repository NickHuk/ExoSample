package com.goat.exosample

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import java.lang.ref.WeakReference

class ExoPool(
  private val context: Context
) {
  private val exoPlayers: MutableList<Player> = mutableListOf()

  @Synchronized
  fun acquire(): Player =
    Player(context)
      .also { player -> exoPlayers.add(player) }

  fun restart() {
    exoPlayers.forEach { it.restart() }
  }

  @Synchronized
  fun release() {
    exoPlayers.forEach { it.releaseExoPlayer() }
  }
}