package cyborg.com.planwork.Class.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.Class.domain.Configuracion;
import cyborg.com.planwork.Class.domain.PlanTrabajo;
import cyborg.com.planwork.Class.utils.AlarmaReceiver;
import cyborg.com.planwork.Class.utils.PDFCreador;
import cyborg.com.planwork.MainActivity;

/**
 * Created by administrador on 18/09/16.
 */
public class Service {
    private SQLiteController sqlite;
    private List<PlanTrabajo> planesTrabajo;
    private PDFCreador pdfCreador;
    private Configuracion configuracion;
    private Context contexto;

    public Service(Context contexto) throws SQLException {
        this.sqlite = new SQLiteController(contexto, "planwork.db", null, 1);
        this.planesTrabajo = new LinkedList<>();
        this.pdfCreador = new PDFCreador();
        this.contexto = contexto;
        cargarConfiguracion();
    }

    public void crearPDF(String nombre, List<Actividad> actividades, String fecha) throws FileNotFoundException, DocumentException {
        cargarConfiguracion();
        pdfCreador.setDatos(nombre, actividades, fecha);
        pdfCreador.setAutor(configuracion.getAutor());
        pdfCreador.crearDocumentoPDF();
    }

    public void addPlanTrabajo(PlanTrabajo pt) throws SQLException {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        sqlite.insertarPlanTrabajo(db, pt.getMes(), pt.getAnno());
    }

    public void setSqlite(SQLiteController sqlite) {
        this.sqlite = sqlite;
    }

    public void setPlanesTrabajo(List<PlanTrabajo> planesTrabajo) {
        this.planesTrabajo = planesTrabajo;
    }

    public SQLiteController getSqlite() {
        return sqlite;
    }

    public List<PlanTrabajo> getPlanesTrabajo() throws SQLException {
        Cursor c = sqlite.listarEntidad(PlanTrabajo.class);
        planesTrabajo.clear();
        if (c.moveToFirst()) {
            do {
                planesTrabajo.add(new PlanTrabajo(c.getInt(0), c.getString(1), c.getInt(2)));
            } while (c.moveToNext());
        }
        c.close();
        return planesTrabajo;
    }

    public void eliminarPlanTrabajo(int id) {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        sqlite.eliminarPlanTrabajo(db, id);
    }

    public void addActividadAPlanTrabajo(int id, Actividad actividad) {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        int avisar = actividad.isAvisar() ? 1 : 0;
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        if (format.format(actividad.getFecha()).equals(format.format(new Date()))) {
            AlarmManager manager = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(contexto, AlarmaReceiver.class);
            intent2.putExtra("datos", new String[]{actividad.getNombre(), actividad.getHora()});
            PendingIntent pIntent = PendingIntent.getBroadcast(contexto, 1, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.setTime(actividad.getFecha());
            String[] hora = actividad.getHora().split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(hora[1]));
            cal.set(Calendar.SECOND, 0);
            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
        }

        sqlite.addActividadAPlanTrabajo(db, id, actividad.getNombre(), format.format(actividad.getFecha()), actividad.getHora(), avisar);
    }

    public int ultimoIDPlanTrabajo() {
        return sqlite.ultimoIDPlanTrabajo();
    }

    //esta
    public List<Actividad> getActividades(int idPt) throws SQLException, ParseException {
        Cursor c = sqlite.listarActividades(Actividad.class, idPt);
        List<Actividad> actividades = new LinkedList<>();

        if (c.moveToFirst()) {
            do {
                boolean avisar = c.getInt(4) == 1;
                String[] aux = c.getString(2).split("/");
                Date fecha = new Date(aux[1] + "/" + aux[0] + "/" + aux[2]);
                Actividad act = new Actividad(c.getInt(0), c.getString(1), fecha, c.getString(3), avisar);
                act.setIdPt(c.getInt(5));
                actividades.add(act);
            } while (c.moveToNext());
        }
        c.close();
        return actividades;
    }

    public List<Actividad> getActividades(int idPt, String dia) throws SQLException, ParseException {
        Cursor c = sqlite.listarActividadesDia(Actividad.class, idPt, dia);
        List<Actividad> actividades = new LinkedList<>();

        if (c.moveToFirst()) {
            do {
                boolean avisar = c.getInt(4) == 1;
                Date fecha = new Date(c.getString(2));
                Actividad act = new Actividad(c.getInt(0), c.getString(1), fecha, c.getString(3), avisar);
                act.setIdPt(c.getInt(5));
                actividades.add(act);
            } while (c.moveToNext());
        }
        c.close();
        return actividades;
    }

    public void eliminarActividad(int id) {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        sqlite.eliminarActividad(db, id);
    }

    public List<Actividad> getActividadAlarmas(String dia) throws SQLException {
        Cursor c = sqlite.listarActividadesDiaAvisar(dia);
        List<Actividad> actividades = new LinkedList<>();

        if (c.moveToFirst()) {
            do {
                boolean avisar = c.getInt(4) == 1;
                Date fecha = new Date(c.getString(2));
                Actividad act = new Actividad(c.getInt(0), c.getString(1), fecha, c.getString(3), avisar);
                act.setIdPt(c.getInt(5));
                actividades.add(act);
            } while (c.moveToNext());
        }
        c.close();
        return actividades;
    }

    public void abrirPDF(Context context) {
        pdfCreador.mostarPDF(context);
    }

    public void cargarConfiguracion() {
        configuracion = new Configuracion();
        Cursor c = sqlite.cargarConfiguracion();
        if (c.moveToFirst()) {
            configuracion.setAutor(c.getString(1));
            configuracion.setCargo(c.getString(2));
        }
        c.close();
    }

    public void setConfiguracion(Configuracion conf) {
        SQLiteDatabase db = sqlite.getWritableDatabase();
        sqlite.setConfiguracion(db, conf.getAutor(), conf.getCargo());
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public boolean existeActividad(int idpt, String nombre, String fecha, String hora) {
        return sqlite.cantidadActividadesIguales(idpt, nombre, fecha, hora) != 0;
    }

    public void sincronizarCalendarioXMes(int idpt) throws SQLException, ParseException, SecurityException {
        ContentResolver cr = contexto.getContentResolver();
        List<Actividad> actList = getActividades(idpt);
        for (Actividad act : actList) {
            ContentValues valores = new ContentValues();
            Calendar cal = Calendar.getInstance();
            cal.setTime(act.getFecha());
            String[] hora = act.getHora().split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(hora[1]));
            cal.set(Calendar.SECOND, 0);
            valores.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
            valores.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
            valores.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            valores.put(CalendarContract.Events.TITLE, act.getNombre());
            valores.put(CalendarContract.Events.CALENDAR_ID, 1);
            Uri insertar = cr.insert(CalendarContract.Events.CONTENT_URI, valores);
        }
    }

    public List<Actividad> actividadesDelCalendario(String fechaPt) throws SecurityException{
        List<Actividad> actList=new LinkedList<>();
        ContentResolver cr = contexto.getContentResolver();
        Date fechaInicio=new Date(fechaPt);
        Date fechaFinal=new Date(fechaPt);
        fechaFinal.setMonth(fechaFinal.getMonth()+1);
        fechaFinal.setDate(fechaFinal.getDate()-1);
        String [] columnas={CalendarContract.Events.DTSTART,CalendarContract.Events.TITLE};
        Cursor cur=cr.query(CalendarContract.Events.CONTENT_URI,columnas,"CalendarContract.Events.CALENDAR_ID=1 and CalendarContract.Events.DTSTART between "+ fechaInicio.getTime()+" and "+ fechaFinal.getTime(),null,null);
        if(cur.moveToFirst()){
            do{
                int nombre=cur.getColumnIndex(CalendarContract.Events.TITLE);
                int fecha=cur.getColumnIndex(CalendarContract.Events.DTSTART);
                Calendar cal=Calendar.getInstance();
                cal.setTime(new Date((Long.parseLong(cur.getString(fecha)))));
                actList.add(new Actividad(0,cur.getString(nombre),cal.getTime(),cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE),false));
            }while (cur.moveToNext());
        }
        return actList;
    }
}
