package cyborg.com.planwork.Class.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.MainActivity;

/**
 * Created by administrador on 10/10/16.
 */
public class ServicioAlarma extends Service{
    //un dia
    private static long tiempoEjecucion=8640000;
    private Timer temporizador;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        temporizador=new Timer();
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        temporizador.schedule(new TimerTask() {
            @Override
            public void run() {

                SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
                String fecha=format.format(c.getTime());
                try {
                    cyborg.com.planwork.Class.controller.Service servicio=new cyborg.com.planwork.Class.controller.Service(getApplicationContext());
                    List<Actividad> actividades= servicio.getActividadAlarmas(fecha);
                    AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    for(Actividad act:actividades){
                        Intent        intent2  = new Intent(getApplicationContext(), AlarmaReceiver.class);
                        intent2.putExtra("datos",new String[]{act.getNombre(),act.getHora()});
                        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent2,  PendingIntent.FLAG_CANCEL_CURRENT);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(act.getFecha());
                        String [] hora=act.getHora().split(":");
                        cal.set (Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
                        cal.set (Calendar.MINUTE, Integer.parseInt(hora[1]));
                        cal.set (Calendar.SECOND, 0);
                        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, c.getTime(), tiempoEjecucion);
    }
}
