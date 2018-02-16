package cyborg.com.planwork.Class.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import cyborg.com.planwork.Class.controller.Service;
import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.Class.domain.PlanTrabajo;
import cyborg.com.planwork.MainActivity;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 18/09/16.
 */
public class CrearPlanTrabajo extends Activity {

    private Service servicio;
    private Spinner mes;
    private EditText anno;
    private static List<Actividad> actividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actividades=new LinkedList<>();
        setContentView(R.layout.app_bar_main_crear);
        servicio=MainActivity.getService();
        String [] meses={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        ArrayAdapter adapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,meses);
        mes=(Spinner) findViewById(R.id.mesCombo);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mes.setAdapter(adapter);
        anno=(EditText)findViewById(R.id.editText2);
        FloatingActionButton crear = (FloatingActionButton) findViewById(R.id.crear);
        final Intent intent=new Intent(this,MainActivity.class);
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int year=Integer.parseInt(anno.getText().toString());
                    if(year>=1970&&year<=2037){
                        servicio.addPlanTrabajo(new PlanTrabajo(0, mes.getSelectedItem().toString(), year));
                        int ultimoId = servicio.ultimoIDPlanTrabajo();
                        addActividadAPlanTrabajo(ultimoId);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(CrearPlanTrabajo.this,"El año debe ser un número\ncomprendido entre 1970 y 2037",Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    Toast.makeText(CrearPlanTrabajo.this,"Ha ocurrido un error en\nla base de datos",Toast.LENGTH_LONG).show();
                }catch (NumberFormatException num){
                    Toast.makeText(CrearPlanTrabajo.this,"Error en el formato del año,\ndebe ser numérico",Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton salir = (FloatingActionButton) findViewById(R.id.salir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(intent);
                    finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    public static List<Actividad> getActividades(){
        return actividades;
    }

    public void addActividadAPlanTrabajo(int idPt){
        for(Actividad act:actividades){
            act.setIdPt(idPt);
            servicio.addActividadAPlanTrabajo(idPt,act);
        }
    }
}
