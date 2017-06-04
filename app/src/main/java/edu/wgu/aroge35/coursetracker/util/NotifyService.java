package edu.wgu.aroge35.coursetracker.util;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import edu.wgu.aroge35.coursetracker.MainActivity;
import edu.wgu.aroge35.coursetracker.R;

/**
 * Created by alissa on 12/4/15.
 */
public class NotifyService extends Service {
    private static final int NOTIFICATION = 100;

    private String title;
    private String text;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        title = intent.getStringExtra("title");
        text = intent.getStringExtra("text");

        createNotification();
        return START_STICKY;
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(text)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION, builder.build());
    }
}
