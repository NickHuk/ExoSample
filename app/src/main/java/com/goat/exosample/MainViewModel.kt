package com.goat.exosample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goat.exosample.epoxy.MediaData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
  private val _media: MutableSharedFlow<List<MediaData>> = MutableSharedFlow(replay = 1)
  val media: Flow<List<MediaData>> = _media

  init {
    viewModelScope.launch(Dispatchers.IO) {
      _media.tryEmit(
        listOf(
          MediaData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
          MediaData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
          MediaData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
          MediaData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
        )
      )
    }
  }

  fun updatePlaybackPosition(holderPosition: Int, playbackPosition: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      _media.replayCache.first()[holderPosition].playbackPosition = playbackPosition
    }
  }
}