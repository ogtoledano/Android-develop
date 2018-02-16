package cyborg.com.planwork.Class.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cyborg.com.planwork.Class.domain.Configuracion;
import cyborg.com.planwork.MainActivity;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 7/10/16.
 */
public class EstablecerConfiguracion extends Activity {

    private EditText autor;
    private EditText cargo;
    private Configuracion conf;
    private FloatingActionButton guardar;
    private FloatingActionButton salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main_configuracion);
        conf=MainActivity.getService().getConfiguracion();
        autor=(EditText)findViewById(R.id.autor);
        autor.setText(conf.getAutor());
        cargo=(EditText)findViewById(R.id.cargo);
        cargo.setText(conf.getCargo());
        guardar=(FloatingActionButton)findViewById(R.id.guardar);
        final Intent intent=new Intent(this,MainActivity.class);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conf.setAutor(autor.getText().toString());
                conf.setCargo(cargo.getText().toString());
                MainActivity.getService().setConfiguracion(conf);
                Toast.makeText(EstablecerConfiguracion.this,"Configuración modificada",Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();
            }
        });
        salir=(FloatingActionButton)findViewById(R.id.salir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(EstablecerConfiguracion.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
