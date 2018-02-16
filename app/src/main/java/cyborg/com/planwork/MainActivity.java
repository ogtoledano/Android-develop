package cyborg.com.planwork;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import cyborg.com.planwork.Class.MyAdapter;
import cyborg.com.planwork.Class.controller.Service;
import cyborg.com.planwork.Class.domain.Configuracion;
import cyborg.com.planwork.Class.domain.PlanTrabajo;
import cyborg.com.planwork.Class.utils.Planificador;
import cyborg.com.planwork.Class.utils.ServicioAlarma;
import cyborg.com.planwork.Class.view.CrearActividad;
import cyborg.com.planwork.Class.view.CrearPlanTrabajo;
import cyborg.com.planwork.Class.view.EstablecerConfiguracion;
import cyborg.com.planwork.Class.view.ListarActividades;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Service servicio;
    private ListView listaPt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            if(servicio==null) {
                servicio = new Service(this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent servicioAlarma=new Intent(MainActivity.this, ServicioAlarma.class);
        startService(servicioAlarma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Intent intent =new Intent(this, CrearPlanTrabajo.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actualizarListaPT();
    }

    @Override
    public void onBackPressed() {
            salirDelSistema();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(MainActivity.this, EstablecerConfiguracion.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.crearpt) {

            Intent intent=new Intent(MainActivity.this,CrearPlanTrabajo.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.configuracion) {
            Intent intent=new Intent(MainActivity.this,EstablecerConfiguracion.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.salir) {
            salirDelSistema();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Service getService(){
        return servicio;
    }

    public void actualizarListaPT(){
        listaPt=(ListView) findViewById(R.id.lista_pt);
        try {
            List<PlanTrabajo> planes=servicio.getPlanesTrabajo();
            MyAdapter adaptador=new MyAdapter(this,planes);
            final AlertDialog.Builder alert =new AlertDialog.Builder(this);
            listaPt.setAdapter(adaptador);
            listaPt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final PlanTrabajo p=(PlanTrabajo) parent.getAdapter().getItem(position);
                    try {
                        p.setActividades(servicio.getActividades(p.getId()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    alert.setTitle("Actividades registradas: "+p.getActividades().size()+"\nMes: "+p.getMes()+" Año: "+p.getAnno());
                    final CharSequence[] items={"Adicionar actividad", "Ver actividades", "Sincronizar con el calendario"};
                    final Intent crearAct=new Intent(MainActivity.this, CrearActividad.class);
                    final Intent listarAct=new Intent(MainActivity.this, ListarActividades.class);
                    crearAct.putExtra("datos",new String[]{p.getAnno()+"",p.getMes(),p.getId()+""});
                    listarAct.putExtra("datos",new String[]{p.getAnno()+"",p.getMes(),p.getId()+""});
                    alert.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case 0:{
                                    startActivity(crearAct);
                                    finish();
                                    break;
                                }
                                case 1:{
                                    startActivity(listarAct);
                                    finish();
                                    break;
                                }
                                case 2:{
                                    Toast.makeText(getApplicationContext(),"Sincronizando...",Toast.LENGTH_LONG).show();
                                    try {
                                        servicio.sincronizarCalendarioXMes(p.getId());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            servicio.eliminarPlanTrabajo(p.getId());
                            actualizarListaPT();
                        }
                    });
                    alert.show();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void salirDelSistema(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cerrar PlanWork")
                .setMessage("Desea realmente salir de la aplicación?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
