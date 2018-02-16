package cyborg.com.planwork.Class.domain;

import java.util.Date;

/**
 * Created by administrador on 17/09/16.
 */
public class Actividad implements Comparable<Actividad>{

    private int id;
    private String nombre;
    private Date fecha;
    private String hora;
    private boolean avisar;
    private int idPt;

    public Actividad(int id, String nombre, Date fecha, String hora, boolean avisar) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.avisar = avisar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isAvisar() {
        return avisar;
    }

    public void setAvisar(boolean avisar) {
        this.avisar = avisar;
    }

    public int getIdPt() {
        return idPt;
    }

    public void setIdPt(int idPt) {
        this.idPt = idPt;
    }

    @Override
    public int compareTo(Actividad o) {
        return this.hora.compareTo(o.getHora());
    }
}
