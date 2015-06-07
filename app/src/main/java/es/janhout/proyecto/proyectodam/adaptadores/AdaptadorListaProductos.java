package es.janhout.proyecto.proyectodam.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.modelos.Producto;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class AdaptadorListaProductos extends ArrayAdapter<Producto>{

    private Context contexto;
    private ArrayList<Producto> datos;
    private int recurso;
    private static LayoutInflater inflador;

    public AdaptadorListaProductos(Context contexto, int recurso, ArrayList<Producto> datos) {
        super(contexto, recurso, datos);
        this.contexto = contexto;
        this.recurso = recurso;
        this.datos = datos;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflador.inflate(recurso, null);
            vh = new ViewHolder();
            vh.nombre = (TextView) convertView.findViewById(R.id.seleccionar_producto_nombre);
            vh.referencia = (TextView) convertView.findViewById(R.id.seleccionar_producto_referencia);
            vh.precio = (TextView) convertView.findViewById(R.id.seleccionar_producto_precio);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.nombre.setText(datos.get(position).getNombre_producto());
        vh.referencia.setText(datos.get(position).getReferencia_producto());
        vh.precio.setText(Metodos.stringToMoney(datos.get(position).getPrecio_venta()));
        return convertView;
    }

    private static class ViewHolder {
        private TextView nombre;
        private TextView referencia;
        private TextView precio;
    }
}