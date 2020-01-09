package es.uvigo.teleco.sleeplug.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import es.uvigo.teleco.sleeplug.MainActivity;
import es.uvigo.teleco.sleeplug.R;
import es.uvigo.teleco.sleeplug.SleePlugApplication;

public class NotificationsUtils extends SleePlugApplication {

    private static final String CHANNEL_ID = "SleePlug";
    private static final String CHANNEL_NAME = "SleePlug Notificaciones";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(true);
    }

    /**
     * Notificación de cuando se activa automáticamente SleePlug
     */
    public static void showIsAutomaticWorking(Context context) {
        if (isSettingNotifications(context)) {
            NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, true);
            nBuilder.setContentText(context.getResources().getString(true ? R.string.notif_is_automatic_working : R.string.notif_off_automatic_working))
                    .setContentIntent(getPendingIntentWithStack(context, MainActivity.class))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
            if (nManager == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.app.NotificationManager");
            }

            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }

    /**
     * Notificación de cuando se activa manualmente SleePlug
     */
    public static void showIsManualWorking(Context context) {
        if (isSettingNotifications(context)) {
            NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, true);
            nBuilder.setContentText(context.getResources().getString(true ? R.string.notif_is_manual_working : R.string.notif_off_manual_working))
                    .setContentIntent(getPendingIntentWithStack(context, MainActivity.class));

            NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
            if (nManager == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.app.NotificationManager");
            }

            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }

    /**
     * Notificación de cuando se activa manualmente durante un período de tiempo SleePlug
     */
    public static void showIsTimerWorking(Context context) {
        if (isSettingNotifications(context)) {
            Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, true);
            nBuilder.setContentText(context.getResources().getString(R.string.notif_is_manual_working))
                    .setContentIntent(getPendingIntentWithStack(context, MainActivity.class))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_stop_black_24dp, "stop", stopPendingIntent);

            NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
            if (nManager == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.app.NotificationManager");
            }

            nManager.notify(NOTIFICATION_ID, nBuilder.build());
        }
    }

    private static NotificationCompat.Builder getBasicNotificationBuilder(Context context, boolean playSound) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (playSound) nBuilder.setSound(notificationSound);
        return nBuilder;
    }

    private static PendingIntent getPendingIntentWithStack(Context context, Class javaClass) {
        Intent resultIntent = new Intent(context, javaClass);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(javaClass);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel(boolean playSound) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = playSound ? NotificationManager.IMPORTANCE_HIGH : NotificationManager.IMPORTANCE_LOW;
            NotificationChannel nChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager nManager = getSystemService(NotificationManager.class);
            nManager.createNotificationChannel(nChannel);

        }
    }

    private static boolean isSettingNotifications(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("sw_notifications", true);
    }
}
