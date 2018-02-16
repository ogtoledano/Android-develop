package cyborg.com.planwork.Class.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import cyborg.com.planwork.Class.domain.Actividad;

/**
 * Created by administrador on 17/09/16.
 */
public class SQLiteController extends SQLiteOpenHelper{

    public SQLiteController(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) throws SQLException {
        super(contexto,nombre,factory,version);
    }

    public void insertarPlanTrabajo(SQLiteDatabase db, Object... valores) throws SQLException{
        String sql ="insert into plantrabajo(mes,anno) values ("+"'"+valores[0]+"', "+valores[1]+")";
        db.execSQL(sql);
        db.close();
    }

    public void eliminarPlanTrabajo(SQLiteDatabase db,int id){
        String sql="delete from plantrabajo where id="+id;
        db.execSQL(sql);
        sql="delete from actividad where id_pt="+id;
        db.execSQL(sql);
        db.close();

    }

    public Cursor listarEntidad(Class<?> clase) throws SQLException{
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM "+clase.getSimpleName().toLowerCase(), null);
        return c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE `actividad` (" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `nombre` TEXT, " +
                " `fecha` TEXT, " +
                " `hora` TEXT, " +
                " `avisar` INTEGER, " +
                " `id_pt` INTEGER NOT NULL " +
                ");";
        db.execSQL(sql);
        sql="CREATE TABLE `plantrabajo` ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `mes` TEXT, " +
                " `anno` INTEGER " +
                ");";
        db.execSQL(sql);
        sql="CREATE TABLE `configuracion` ( " +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " `autor` TEXT, " +
                " `cargo` TEXT " +
                ");";
        String sql2= "insert into configuracion(autor,cargo) values ('<sin_nombre>','<sin_cargo>')";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    public void addActividadAPlanTrabajo(SQLiteDatabase db,int id,Object... args){
        String sql="insert into actividad(nombre,fecha,hora,avisar,id_pt) values ("+"'"+args[0]+"', "+"'"+args[1]+"', "+"'"+args[2]+"', "+args[3]+", "+id+")";
        db.execSQL(sql);
        db.close();
    }

    public void setConfiguracion(SQLiteDatabase db,Object... args){
        String sql = "update configuracion set autor="+"'"+args[0]+"'"+ ", cargo="+"'"+args[1]+"'";
        db.execSQL(sql);
        db.close();
    }

    public Cursor cargarConfiguracion(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM configuracion", null);
        return c;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int ultimoIDPlanTrabajo(){
        SQLiteDatabase db=getReadableDatabase();
        String sql="select max(id) from plantrabajo";
        Cursor c=db.rawQuery(sql,null);
        if(c.moveToFirst()) {
            return c.getInt(0);
        }else{
            return 0;
        }
    }

    public Cursor listarActividades(Class<?> clase, int idPt) throws SQLException{
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM "+clase.getSimpleName().toLowerCase()+" where id_pt= "+idPt, null);
        return c;
    }

    public Cursor listarActividadesDia(Class<?> clase, int idPt,String dia) throws SQLException{
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM "+clase.getSimpleName().toLowerCase()+" where id_pt= "+idPt+ " and fecha="+"'"+dia+"'" +" order by hora asc", null);
        return c;
    }

    public void eliminarActividad(SQLiteDatabase db,int id){
        String sql="delete from actividad where id="+id;
        db.execSQL(sql);
        db.close();
    }

    public Cursor listarActividadesDiaAvisar(String dia) throws SQLException{
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM actividad where fecha="+"'"+dia+"'"+" and avisar=1", null);
        return c;
    }

    public int cantidadActividadesIguales(int idpt,Object... args){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT count(*) FROM actividad where nombre="+"'"+args[0]+"'"+" and fecha="+"'"+args[1]+"'"+" and hora="+"'"+args[2]+"'"+" and id_pt="+idpt, null);
        if(c.moveToFirst()) {
            return c.getInt(0);
        }else{
            return 0;
        }
    }
}
