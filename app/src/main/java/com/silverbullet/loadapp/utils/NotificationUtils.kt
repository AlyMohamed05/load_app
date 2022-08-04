package com.silverbullet.loadapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.silverbullet.loadapp.R
import com.silverbullet.loadapp.ui.Result
import com.silverbullet.loadapp.ui.ResultActivity

private const val DOWNLOAD_NOTIFICATION_ID = 0
private const val RESULT_NOTIFICATION_ID = 1

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createChannel(
    id: String,
    name: String,
    description: String? = null,
    importance: Int
) {
    val channel = NotificationChannel(id, name, importance)
    description?.let { channel.description = it }
    createNotificationChannel(channel)
}

fun NotificationManager.sendDownloadProgressNotification(context: Context) {
    val builder =
        NotificationCompat.Builder(context, context.getString(R.string.download_status_channel_id))
            .apply {
                priority = NotificationCompat.PRIORITY_HIGH
                setSmallIcon(R.drawable.ic_download_notification)
                setContentTitle("Downloading")
                setProgress(0, 0, true)
            }
    notify(DOWNLOAD_NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelDownloadProgressNotification() {
    cancel(DOWNLOAD_NOTIFICATION_ID)
}

fun NotificationManager.sendResultNotification(context: Context, event: Result) {
    val intent = Intent(context, ResultActivity::class.java)
    val fileName = if (event is Result.Success || event is Result.Failed) {
        when (event.id) {
            R.id.glide_download_button -> context.getString(R.string.item_1_text)
            R.id.loadapp_download_button -> context.getString(R.string.item_2_text)
            R.id.retrofit_download_button -> context.getString(R.string.item_3_text)
            else -> ""
        }
    } else {
        ""
    }
    when (event) {
        is Result.Success -> {
            intent.putExtra("fileName", fileName)
            intent.putExtra("status", "Success")
        }
        is Result.Failed -> {
            intent.putExtra("fileName", "fileName")
            intent.putExtra("status", "Failed")
        }
        Result.IDLE -> {}
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        1000,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder =
        NotificationCompat.Builder(context, context.getString(R.string.download_result_channel_id))
            .apply {
                priority = NotificationCompat.PRIORITY_MAX
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle("Result")
                val text = if (event is Result.Success) "Download Succeeded" else "Download failed"
                setContentText(text)
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }
    notify(RESULT_NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelResultNotification() {
    cancel(RESULT_NOTIFICATION_ID)
}