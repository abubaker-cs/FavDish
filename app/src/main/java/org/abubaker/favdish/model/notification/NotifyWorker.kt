package org.abubaker.favdish.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.abubaker.favdish.R
import org.abubaker.favdish.utils.Constants
import org.abubaker.favdish.view.activities.MainActivity

class NotifyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {

        Log.i("Notify Worker", "doWork function is called....")

        // Call the function to trigger the notification when the doWork is called.
        sendNotification()

        return success()
    }

    /**
     * A function to send the notification.
     */
    private fun sendNotification() {

        // Add the notification id.
        // In our case the notification id is 0. If you are dealing with dynamic functionality then you can have it as unique for every notification.
        val notification_id = 0

        // TODO Step 4: Create an intent instance that we want to navigate the user when it is clicked.
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Pass the notification id as intent extra to handle the code when user is navigated in the app with notification.
        intent.putExtra(Constants.NOTIFICATION_ID, notification_id)

        // Create an instance of Notification Manager.
        /**
         * Class to notify the user of events that happen.  This is how you tell
         * the user that something has happened in the background.
         */
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Define the Notification Title and SubTitle.
        val titleNotification = applicationContext.getString(R.string.notification_title)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)

        // Generate the bitmap from vector icon using the function that we have created.
        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)

        // Create the style of the Notification.
        // You can create the style as you want here we will create a notification using BigPicture.
        // For Example InboxStyle() which is used for simple Text message.

        // The style of the Notification. You can create the style as you want here we will create a notification using BigPicture.
        // For Example InboxStyle() which is used for simple Text message.
        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null) // The null is passed to avoid the duplication of image when the notification is en-large from notification tray.

        // Define the pending intent for Notification.
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        // Before building the Notification Builder add the notification icon. You can have look the note file where I have mentioned the step How to generate it.

        // Now as we most of the required params so lets build the Notification Builder.
        val notification =
            /**
             * @param context A {@link Context} that will be used to construct the
             *      RemoteViews. The Context will not be held past the lifetime of this
             *      Builder object.
             * @param channelId The constructed Notification will be posted on this
             *      NotificationChannel.
             */
            NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)

                // Set the Notification Title
                .setContentTitle(titleNotification)

                // Set the Notification SubTitle
                .setContentText(subtitleNotification)

                // Set the small icon also you can say as notification icon that we have generated.
                .setSmallIcon(R.drawable.ic_stat_notification)

                // Set the Large icon
                .setLargeIcon(bitmap)

                // Set the default notification options that will be used.
                .setDefaults(NotificationCompat.DEFAULT_ALL)

                // Supply a PendingIntent to send when the notification is clicked.
                .setContentIntent(pendingIntent)

                // Add a rich notification style to be applied at build time.
                .setStyle(bigPicStyle)

                // Setting this flag will make it so the notification is automatically canceled when the user clicks it in the panel.
                .setAutoCancel(true)

        // Set the Priority fo the notification.
        notification.priority = NotificationCompat.PRIORITY_MAX

        // Set channel ID for Notification if you are using the API level 26 or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

            // Setup the Ringtone for Notification.
            val ringtoneManager =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    Constants.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        // Notify the user with Notification id and Notification builder using the NotificationManager instance that we have created.
        notificationManager.notify(notification_id, notification.build())
    }

    // Create a function that will convert the vector image to bitmap as below.
    /**
     * A function that will convert the vector image to bitmap as below.
     */
    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        // Get the Drawable Vector Image
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}