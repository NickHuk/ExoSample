package com.goat.exosample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Util
import androidx.recyclerview.widget.LinearLayoutManager
import com.goat.exosample.databinding.ActivityMainBinding
import com.goat.exosample.epoxy.EpoxyExoController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MainActivity : AppCompatActivity() {
  private val viewModel: MainViewModel by viewModels()
  private val exoPool: ExoPool by lazy {
    ExoPool(this)
  }
  private val playbackManager by lazy {
    PlaybackManager()
  }
  private val controller: EpoxyExoController by lazy {
    EpoxyExoController(
      exoPool,
      playbackManager,
      viewModel::updatePlaybackPosition,
    )
  }
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater).also { view -> setContentView(view.root) }
    binding.videos.apply {
      layoutManager = LinearLayoutManager(this@MainActivity)
      adapter = controller.adapter
      addItemDecoration(SpaceItemDecoration())
    }
    viewModel.media
      .onEach { media -> controller.setData(media) }
      .launchIn(lifecycleScope)
  }

  override fun onPause() {
    super.onPause()
    if (Util.SDK_INT <= 23) {
      Timber.tag(VIDEO_LIST).d("release pool")
      playbackManager.release()
      exoPool.releaseAll()
    }
  }

  override fun onStop() {
    super.onStop()
    if (Util.SDK_INT > 23) {
      Timber.tag(VIDEO_LIST).d("release pool")
      playbackManager.release()
      exoPool.releaseAll()
    }
  }

  override fun onStart() {
    super.onStart()
    if (Util.SDK_INT > 23) {
      Timber.tag(VIDEO_LIST).d("restart pool")
      playbackManager.restart()
    }
  }

  override fun onResume() {
    super.onResume()
    if (Util.SDK_INT <= 23) {
      Timber.tag(VIDEO_LIST).d("restart pool")
      playbackManager.restart()
    }
  }
}