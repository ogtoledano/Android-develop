package cyborg.com.planwork.Class.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import cyborg.com.planwork.Class.MyAdapterActividad;
import cyborg.com.planwork.Class.controller.Service;
import cyborg.com.planwork.Class.domain.Actividad;
import cyborg.com.planwork.MainActivity;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 21/09/16.
 */
public class ListarActividades extends Activity {

    private ListView listaPt;
    private CalendarView calendar;
    private Service servicio;
    private String[] extra;
    private FloatingActionButton crear;
    private FloatingActionButton pdfCrear;
    private FloatingActionButton cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        servicio = MainActivity.getService();
        setContentView(R.layout.app_bar_main_listar_act);
        listaPt = (ListView) findViewById(R.id.lista_act);
        calendar = (CalendarView) findViewById(R.id.calendario);
        extra = getIntent().getStringArrayExtra("datos");
        final Date fecha = new Date(Integer.parseInt(extra[0]) - 1900, mes(extra[1]), 1);
        actualizarLista();
        calendar.setDate(fecha.getTime());
        crear = (FloatingActionButton) findViewById(R.id.crear);
        final Intent crearAct = new Intent(this, CrearActividad.class);
        crearAct.putExtra("datos", new String[]{extra[0] + "", extra[1], extra[2] + ""});
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(crearAct);
                finish();
            }
        });
        final Intent main = new Intent(this, MainActivity.class);
        cerrar = (FloatingActionButton) findViewById(R.id.salir);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(main);
                finish();
            }
        });
        pdfCrear=(FloatingActionButton)findViewById(R.id.pdfCrear);
        pdfCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    servicio.crearPDF("Plan de trabajo ["+extra[1]+"-"+extra[0]+"]",servicio.getActividades(Integer.parseInt(extra[2])),mes(extra[1])+1+"/1/"+extra[0]);
                    AlertDialog.Builder alert=new AlertDialog.Builder(ListarActividades.this);
                    alert.setTitle("Documento creado");
                    alert.setMessage("Desea abrir el nuevo documento\n"+"Plan trabajo ["+extra[1]+"-"+extra[0]+"]");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            servicio.abrirPDF(ListarActividades.this);
                        }
                    });
                    alert.setNeutralButton(android.R.string.no,null);
                    alert.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*AlertDialog.Builder alertPerso=new AlertDialog.Builder(getApplicationContext());
                LayoutInflater inflater=getLayoutInflater();
                alertPerso.setView(inflater.inflate(R.layout.dialog_crear_pdf,null));
                final EditText nombre=(EditText)findViewById(R.id.nombre);
                alertPerso.setNegativeButton("Cancelar",null);
                alertPerso.setPositiveButton("Crear pdf", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog alert=alertPerso.create();
                alert.show();*/
            }
        });
    }

    private int mes(String mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equalsIgnoreCase(mes)) {
                return i;
            }
        }
        return -1;
    }

    public void actualizarLista() {
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, final int year, final int month, final int dayOfMonth) {
                try {
                    final List<Actividad> datos = servicio.getActividades(Integer.parseInt(extra[2]), dayOfMonth + "/" + (month + 1) + "/" + year);
                    final MyAdapterActividad adapter = new MyAdapterActividad(ListarActividades.this, datos);
                    listaPt.setAdapter(adapter);
                    listaPt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ListarActividades.this);
                            dialog.setTitle("Acciones");
                            final Actividad a = datos.get(position);
                            dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialog.setMessage("Nombre de actividad: " + a.getNombre());
                            dialog.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    servicio.eliminarActividad(a.getId());
                                    datos.remove(position);
                                    MyAdapterActividad adapter = new MyAdapterActividad(ListarActividades.this, datos);
                                    listaPt.setAdapter(adapter);
                                }
                            });
                            dialog.show();
                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


}
