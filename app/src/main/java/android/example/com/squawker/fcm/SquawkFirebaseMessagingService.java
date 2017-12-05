package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by tareq on 5/12/17.
 */

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;

    private static final String TAG = SquawkFirebaseMessagingService.class.getSimpleName();
    private static final int NOTIFICATION_MAX_CHARACTERS = 30;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "onMessageReceived: From: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();
         if (data.size() > 0) {
            Log.d(TAG, "onMessageReceived: data: "+ data);
            sendNotification(data);
            insertSquawk(data);
         }

    }

    private void insertSquawk (final Map<String, String> data) {
        AsyncTask asyncTask;
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ContentValues values = new ContentValues();
                values.put(SquawkContract.COLUMN_AUTHOR,data.get(JSON_KEY_AUTHOR));
                values.put(SquawkContract.COLUMN_AUTHOR_KEY,data.get(JSON_KEY_AUTHOR_KEY));
                values.put(SquawkContract.COLUMN_MESSAGE,data.get(JSON_KEY_MESSAGE));
                values.put(SquawkContract.COLUMN_DATE,data.get(JSON_KEY_DATE));
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI,values);
                return null;
            }
        };

        asyncTask.execute();
    }

    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String author = data.get(JSON_KEY_AUTHOR);
        String message = data.get(JSON_KEY_MESSAGE);

        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message),author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }
}
