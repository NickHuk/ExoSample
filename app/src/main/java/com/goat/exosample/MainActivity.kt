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
  private val viewModel: MainViewModel by viewModels(
    factoryProducer = { MainViewModelFactory(applicationContext) }
  )

  private val controller: EpoxyExoController by lazy {
    EpoxyExoController(
      viewModel.exoPool,
      viewModel.playbackManager,
      viewModel::updatePlaybackPosition,
    )
  }
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.tag(VIDEO_LIST).d("activity onCreate")
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

  override fun onStart() {
    super.onStart()
    if (Util.SDK_INT > 23) {
      Timber.tag(VIDEO_LIST).d("restart pool")
      viewModel.playbackManager.restart()
    }
  }

  override fun onResume() {
    super.onResume()
    if (Util.SDK_INT <= 23) {
      Timber.tag(VIDEO_LIST).d("restart pool")
      viewModel.playbackManager.restart()
    }
  }

  override fun onPause() {
    if (Util.SDK_INT <= 23) {
      Timber.tag(VIDEO_LIST).d("release pool pause")
      viewModel.playbackManager.release()
      viewModel.exoPool.releaseAll()
    }
    super.onPause()
  }

  override fun onStop() {
    if (Util.SDK_INT > 23) {
      Timber.tag(VIDEO_LIST).d("release pool stop")
      viewModel.playbackManager.release()
      viewModel.exoPool.releaseAll()
    }
    super.onStop()
  }

  override fun onDestroy() {
    Timber.tag(VIDEO_LIST).d("activity destroyed")
    super.onDestroy()
  }
}