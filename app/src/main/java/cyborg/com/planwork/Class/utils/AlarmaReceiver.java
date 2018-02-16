package cyborg.com.planwork.Class.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import cyborg.com.planwork.MainActivity;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 26/09/16.
 */
public class AlarmaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String [] datos=intent.getStringArrayExtra("datos");
        Vibrator vibrator =(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        NotificationCompat.Builder mBuilder =

                new NotificationCompat.Builder(context)

                        .setSmallIcon(android.R.drawable.ic_menu_my_calendar)

                        .setLargeIcon((((BitmapDrawable)context.getResources()

                                .getDrawable(R.mipmap.main_ico)).getBitmap()))

                        .setContentTitle(datos[0])

                        .setContentText("Hora: "+datos[1])

                        .setContentInfo("Recordatorio")

                        .setTicker("PlanWork!")
                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,"1"));

        Intent notIntent = new Intent(context, MainActivity.class);

        PendingIntent contIntent = PendingIntent.getActivity(

                context, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //cambiar id del notify
        mNotificationManager.notify(Integer.MAX_VALUE, mBuilder.build());

    }
}
