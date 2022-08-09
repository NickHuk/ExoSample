package com.goat.exosample

import android.content.Context
import timber.log.Timber
import java.lang.ref.WeakReference

class ExoPool(private val context: Context) {
  private val lockedExoPlayers: MutableList<Player> = mutableListOf()
  private val freeExoPlayers: MutableList<Player> = mutableListOf(Player(context))

  @Synchronized
  fun acquire(): WeakReference<Player> =
    if(freeExoPlayers.isEmpty()) {
      WeakReference(Player(context).also(lockedExoPlayers::add))
    } else {
      WeakReference(freeExoPlayers.removeLast().also(lockedExoPlayers::add))
    }.also {
      Timber.tag(VIDEO_LIST).d(
        "pool size: %d / %d",
        lockedExoPlayers.size,
        lockedExoPlayers.size + freeExoPlayers.size
      )
    }

  @Synchronized
  fun stop(player: Player) {
    player.stopExoPlayer()
    lockedExoPlayers.remove(player)
    if(player.used < 2)
      freeExoPlayers += player
  }

  @Synchronized
  fun releaseAll() {
    lockedExoPlayers.forEach { player ->
      player.releaseExoPlayer()
    }
    lockedExoPlayers.clear()
  }
}