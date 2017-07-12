package br.com.vitorota.naodurmanoponto.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import br.com.vitorota.naodurmanoponto.MyApp;
import br.com.vitorota.naodurmanoponto.R;
import br.com.vitorota.naodurmanoponto.view.MainActivity;

/**
 * Created by Vitor Ota on 22/06/2016.
 */
public class MessageUtil {
    public static void showNotification(String title, String message, Class<? extends Activity> classe){
        Context c = MyApp.getAppContext();
        Notification n = new Notification.Builder(c)
                .setContentTitle(title)
                .setContentInfo(message)
                .build();

        Intent intent = new Intent(c, classe.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
}
