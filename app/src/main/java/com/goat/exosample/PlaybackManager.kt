package com.goat.exosample

class PlaybackManager {

  val restartListeners: MutableList<OnRestartListener> = mutableListOf()

  fun release() {
    for(listener in restartListeners) {
      listener.onRelease()
    }
  }

  fun restart() {
    for(listener in restartListeners) {
      listener.onRestart()
    }
  }

  fun addOnRestartListener(listener: OnRestartListener) {
    restartListeners += listener
  }

  fun detachListener(listener: OnRestartListener)  {
    restartListeners.remove(listener)
  }
}

interface OnRestartListener {
  fun onRelease()
  fun onRestart()
}