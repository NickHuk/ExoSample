package com.goat.exosample

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
  private val _media: MutableStateFlow<List<String>> =
    MutableStateFlow(
      listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
      )
    )
  val media: Flow<List<String>> = _media

  private val _playbackPositions: MutableStateFlow<List<Long>> = MutableStateFlow(
    listOf(0L, 0L, 0L, 0L)
  )
  val playbackPositions: Flow<List<Long>> = _playbackPositions

  fun updatePlaybackPosition(holderPosition: Int, playbackPosition: Long) {
    _playbackPositions.update { playbackPositions ->
      playbackPositions.toMutableList().apply {
        removeAt(holderPosition)
        add(holderPosition, playbackPosition)
      }
    }
  }
}