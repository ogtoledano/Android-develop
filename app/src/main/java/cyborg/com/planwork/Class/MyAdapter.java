package cyborg.com.planwork.Class;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cyborg.com.planwork.Class.domain.PlanTrabajo;
import cyborg.com.planwork.R;

/**
 * Created by administrador on 20/09/16.
 */
public class MyAdapter extends ArrayAdapter {
    Activity context;

    public MyAdapter(Activity context, List datos) {
        super(context, R.layout.elemento_lista, datos);
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.elemento_lista, null);
        TextView lblTitulo = (TextView)item.findViewById(R.id.titulo1);
        lblTitulo.setText(((PlanTrabajo)super.getItem(position)).getMes());
        TextView lblSubtitulo = (TextView)item.findViewById(R.id.titulo2);
        lblSubtitulo.setText(""+((PlanTrabajo)super.getItem(position)).getAnno());
        return(item);
    }
}

