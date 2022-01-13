package com.example.photomqtt;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DelayedMessageService extends IntentService {
    public static final String EXTRA_MESSAGE = "message";
    public static  int NOTIFICATION_ID = 0;


    public DelayedMessageService() {
        super("DelayedMessageService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service","onHandleIntent start");
            synchronized (this) {
                try {
                    wait(1000); // подождать 1 сек
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        //String text = "Все ок с моим здоровьем";//intent.getStringExtra(EXTRA_MESSAGE);
        String text =intent.getStringExtra(EXTRA_MESSAGE);
        showText(text);

    }
    private void showText(final String text) {

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class); // правильная работа кнопки назад
        stackBuilder.addNextIntent(intent);
        // создать отложенный интент
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        CharSequence verseurl = "";
        // стили уведомлений
        bigText.bigText(verseurl);
        bigText.setBigContentTitle(text);
        bigText.setSummaryText("900");
        // добавление интента в уведомление
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("900 "+text); // задать заголовок
        mBuilder.setContentText("");  // задать текст
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // идентификатор, назначаемый уведомлению
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        //Вывести созданное нами уведомление через службу уведомлений
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        ++NOTIFICATION_ID;



    }



}
