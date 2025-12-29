package com.example.notecook.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.notecook.R;

public class NotificationUtils {

    public static void showNotification(
            @NonNull Context context,
            @NonNull Class<?> targetActivity,
            @NonNull String title,
            @NonNull String message,
            int notificationId
    ) {
        String CHANNEL_ID = "my_channel_01";
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "My App Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("General notifications");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        nm.createNotificationChannel(channel);

        Intent intent = new Intent(context, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_icon_app)  // أو drawable اللي عندك
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        nm.notify(notificationId, builder.build());
    }
}

