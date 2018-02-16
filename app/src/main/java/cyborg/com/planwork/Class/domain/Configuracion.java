package cyborg.com.planwork.Class.domain;

/**
 * Created by administrador on 7/10/16.
 */
public class Configuracion {
    private String autor;
    private String cargo;

    public Configuracion() {
        this.cargo = "<no definido>";
        this.autor = "<sin nombre>";
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
