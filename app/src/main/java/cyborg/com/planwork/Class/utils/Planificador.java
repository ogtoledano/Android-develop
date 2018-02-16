package cyborg.com.planwork.Class.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.MainActivity;

/**
 * Created by administrador on 27/09/16.
 */
public class Planificador extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent servicio = new Intent(context, ServicioAlarma.class);
        context.startService(servicio);
    }

}
