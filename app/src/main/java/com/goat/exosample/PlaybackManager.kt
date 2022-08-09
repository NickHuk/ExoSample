package com.goat.exosample

class PlaybackManager {

  val restartListeners: MutableList<OnRestartListener> = mutableListOf()

  fun release() {
    restartListeners.forEach { it.onRelease() }
  }

  fun restart() {
    restartListeners.forEach { it.onRestart() }
  }

  fun addOnRestartListener(listener: OnRestartListener) {
    restartListeners += listener
  }

  fun detachListener(listener: OnRestartListener)  {
    restartListeners.remove(listener)
  }

  fun clear() {
    restartListeners.clear()
  }
}

interface OnRestartListener {
  fun onRelease()
  fun onRestart()
}