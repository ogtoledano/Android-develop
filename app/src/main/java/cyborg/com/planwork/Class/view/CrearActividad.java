package cyborg.com.planwork.Class.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import cyborg.com.planwork.Class.controller.Service;
import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.MainActivity;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 21/09/16.
 */
public class CrearActividad extends Activity {
    private Service servicio;
    private EditText nombre;
    private TimePicker hora;
    private CheckBox avisar;
    private CalendarView calendario;
    private FloatingActionButton crear;
    private FloatingActionButton salir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main_crear_actividad);
        servicio=MainActivity.getService();
        nombre=(EditText)findViewById(R.id.editText2);
        hora=(TimePicker)findViewById(R.id.timePicker);
        avisar=(CheckBox)findViewById(R.id.checkBox);
        calendario=(CalendarView)findViewById(R.id.calendario);
        final String[] datos=getIntent().getStringArrayExtra("datos");
        final Date fecha=new Date(Integer.parseInt(datos[0])-1900,mes(datos[1]),1);
        calendario.setDate(fecha.getTime());
        crear=(FloatingActionButton)findViewById(R.id.crear);
        salir=(FloatingActionButton)findViewById(R.id.salir);
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nombre.getText().toString().equals("")) {
                    String minutos = hora.getCurrentMinute() + "";
                    String horas = hora.getCurrentHour() + "";
                    if (horas.length() == 1) {
                        horas = "0" + horas;
                    }
                    if (minutos.length() == 1) {
                        minutos = "0" + minutos;
                    }
                    fecha.setTime(calendario.getDate());
                    SimpleDateFormat format=new SimpleDateFormat("d/M/yyyy");
                    if(!servicio.existeActividad(Integer.parseInt(datos[2]),nombre.getText().toString(),format.format(fecha),horas + ":" + minutos)) {
                        servicio.addActividadAPlanTrabajo(Integer.parseInt(datos[2]), new Actividad(0, nombre.getText().toString(), fecha, horas + ":" + minutos, avisar.isChecked()));
                        Toast.makeText(getApplicationContext(), "Actividad creada correctamente", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "La actividad ya existe para ese día\ncon la misma hora", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CrearActividad.this,"El nombre de la actividad no puede ser vacío",Toast.LENGTH_LONG).show();
                }
            }
        });
        final Intent mainAct=new Intent(this,MainActivity.class);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainAct);
                finish();
            }
        });
    }

    private int mes(String mes){
        String [] meses= {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        for(int i=0;i<meses.length;i++){
            if(meses[i].equalsIgnoreCase(mes)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        final Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
