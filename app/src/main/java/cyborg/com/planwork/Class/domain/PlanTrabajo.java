package cyborg.com.planwork.Class.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by administrador on 17/09/16.
 */
public class PlanTrabajo {

    private int id;
    private String mes;
    private int anno;
    private List<Actividad> actividades;

    public PlanTrabajo(int id,String mes, int anno) {
        this.id=id;
        this.mes = mes;
        this.anno = anno;
        this.actividades=new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    public void addActividad(Actividad actividad){
        this.actividades.add(actividad);
    }
}
