package com.silverbullet.loadapp.ui

import android.animation.*
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.silverbullet.loadapp.R
import com.silverbullet.loadapp.custom_views.CircularIndicator
import com.silverbullet.loadapp.databinding.ActivityHomeBinding
import com.silverbullet.loadapp.utils.cancelDownloadProgressNotification
import com.silverbullet.loadapp.utils.createChannel
import com.silverbullet.loadapp.utils.sendDownloadProgressNotification
import com.silverbullet.loadapp.utils.sendResultNotification
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private var notificationManager: NotificationManager? = null
    private lateinit var animation: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        initOnClickListeners()
        initObservers()
        initAnimation()
        initNotificationManager()
        initNotificationChannels()
    }

    private fun initOnClickListeners() {
        binding.downloadButton.setOnClickListener {
            val selectedID = binding.downloadSelectorRadioGroup.checkedRadioButtonId
            if (selectedID == -1) {
                Toast.makeText(
                    this@HomeActivity,
                    "Please select a file to download",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                homeViewModel.download(selectedID)
            }
        }
    }

    private fun initObservers() {
        homeViewModel.isDownloading.observe(this, Observer { handleLoadingEvent(it) })
        homeViewModel.responseEvent.observe(this, Observer { handleResponseEvent(it) })
    }

    private fun handleLoadingEvent(isLoading: Boolean?) {
        if (isLoading == null) {
            return
        }
        if (isLoading) {
            notificationManager?.sendDownloadProgressNotification(this)
            animateForeWard()
        } else {
            notificationManager?.cancelDownloadProgressNotification()
            animateBackward()
        }
    }

    private fun handleResponseEvent(event: Result) {
        if (event is Result.IDLE) {
            return
        }
        notificationManager?.sendResultNotification(applicationContext, event)
    }

    private fun initNotificationManager() {
        if (Build.VERSION.SDK_INT > 23) {
            notificationManager = getSystemService(NotificationManager::class.java)
        }
    }

    private fun initAnimation() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.01f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.01f)
        val rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 720f)
        val downloadButtonAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.downloadButton,
            scaleX,
            scaleY,
            rotation
        )
        downloadButtonAnimator.handleButtonVisibility(binding.downloadButton)

        val indicatorScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.01f, 1f)
        val indicatorScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.01f, 1f)
        val indicatorAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.progressCircular,
            indicatorScaleX, indicatorScaleY
        )
        indicatorAnimator.handleIndicatorVisibility(binding.progressCircular)

        animation = AnimatorSet()
        animation.playSequentially(downloadButtonAnimator, indicatorAnimator)
    }

    private fun animateForeWard() {
        animation.start()
    }

    private fun animateBackward() {
        if(Build.VERSION.SDK_INT>26){
            animation.reverse()
        }
    }

    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Downloading Status channel.
            val downloadStatusChannelID = getString(R.string.download_status_channel_id)
            val downloadStatusChannelName = getString(R.string.download_status_channel_name)
            val downloadStatusChannelDescription =
                getString(R.string.download_status_channel_description)
            notificationManager.createChannel(
                downloadStatusChannelID,
                downloadStatusChannelName,
                downloadStatusChannelDescription,
                NotificationManagerCompat.IMPORTANCE_HIGH
            )

            // Downloads Result channel.
            val downloadResultChannelID = getString(R.string.download_result_channel_id)
            val downloadResultChannelName = getString(R.string.download_result_channel_name)
            val downloadResultChannelDescription =
                getString(R.string.download_result_channel_description)
            notificationManager.createChannel(
                downloadResultChannelID,
                downloadResultChannelName,
                downloadResultChannelDescription,
                NotificationManagerCompat.IMPORTANCE_MAX
            )
        }
    }

    /**
     * hides view when animation is forward.
     * if animation is running, in backward it shows the view
     */
    private fun ObjectAnimator.handleButtonVisibility(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                if (isReverse) {
                    view.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                if (!isReverse) {
                    view.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun ObjectAnimator.handleIndicatorVisibility(indicator: CircularIndicator) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                if (!isReverse) {
                    indicator.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                if (isReverse) {
                    indicator.visibility = View.INVISIBLE
                }else{
                    indicator.playAnimation()
                }
            }
        })
    }
}