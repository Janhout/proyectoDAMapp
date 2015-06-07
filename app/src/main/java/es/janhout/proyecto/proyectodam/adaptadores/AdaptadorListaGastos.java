package es.janhout.proyecto.proyectodam.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.modelos.Gasto;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class AdaptadorListaGastos extends ArrayAdapter<Gasto> {

    private Context contexto;
    private ArrayList<Gasto> datos;
    private int recurso;
    private static LayoutInflater inflador;

    public AdaptadorListaGastos(Context contexto, int recurso, ArrayList<Gasto> datos) {
        super(contexto, recurso, datos);
        this.contexto = contexto;
        this.recurso = recurso;
        this.datos = datos;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflador.inflate(recurso, null);
            vh = new ViewHolder();
            vh.fechaGasto = (TextView) convertView.findViewById(R.id.fechaGasto);
            vh.importeGasto = (TextView) convertView.findViewById(R.id.importeGasto);
            vh.concepto = (TextView) convertView.findViewById(R.id.conceptoGasto);
            vh.fotoGasto = (TextView) convertView.findViewById(R.id.icono_gastoFoto);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if(datos.get(position).getRutaFoto() != null &&
                !datos.get(position).getRutaFoto().trim().equals("")){
            Metodos.textViewAwesomeComponente(contexto, vh.fotoGasto, contexto.getString(R.string.icono_camara));
        } else {
            vh.fotoGasto.setText("");
        }
        vh.concepto.setText(datos.get(position).getConcepto());
        vh.fechaGasto.setText(datos.get(position).getFecha());
        vh.importeGasto.setText(Metodos.stringToMoney(datos.get(position).getImporte()));

        return convertView;
    }

    public static class ViewHolder {
        public TextView concepto;
        public TextView importeGasto;
        public TextView fechaGasto;
        public TextView fotoGasto;
    }
}