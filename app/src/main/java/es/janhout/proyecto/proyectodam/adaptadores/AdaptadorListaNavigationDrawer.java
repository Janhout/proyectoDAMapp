package es.janhout.proyecto.proyectodam.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.modelos.ItemNavigationDrawer;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class AdaptadorListaNavigationDrawer extends ArrayAdapter {

    private Context contexto;
    private int recurso;
    private static LayoutInflater inflador;

    public AdaptadorListaNavigationDrawer(Context contexto, int recurso, List<ItemNavigationDrawer> objects) {
        super(contexto, recurso, objects);
        this.recurso = recurso;
        this.contexto = contexto;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflador.inflate(recurso, null);
            vh = new ViewHolder();
            vh.icono = (TextView) convertView.findViewById(R.id.iconoElemento);
            vh.titulo = (TextView) convertView.findViewById(R.id.tituloElemento);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ItemNavigationDrawer item = (ItemNavigationDrawer)getItem(position);
        Metodos.textViewAwesomeComponente(contexto, vh.icono, item.getIcono());
        vh.titulo.setText(item.getNombre());

        return convertView;
    }
    public static class ViewHolder {
        public TextView titulo;
        public TextView icono;
    }
}

