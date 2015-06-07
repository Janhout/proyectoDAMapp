package es.janhout.proyecto.proyectodam.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.modelos.Factura;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class AdaptadorListaFacturas extends ArrayAdapter<Factura> {

    private Context contexto;
    private ArrayList<Factura> datos;
    private int recurso;
    private static LayoutInflater inflador;

    public AdaptadorListaFacturas(Context contexto, int recurso, ArrayList<Factura> datos) {
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
            vh.numeroFactura = (TextView) convertView.findViewById(R.id.numeroFactura);
            vh.fechaFactura = (TextView) convertView.findViewById(R.id.fechaFactura);
            vh.importeFactura = (TextView) convertView.findViewById(R.id.importeFactura);
            vh.importePendiente = (TextView) convertView.findViewById(R.id.importePendiente);
            vh.iconoEnviado = (TextView) convertView.findViewById(R.id.detalle_factura_iconoEnviado);
            vh.iconoImpreso = (TextView) convertView.findViewById(R.id.detalle_factura_iconoImpreso);
            vh.nombreCliente = (TextView) convertView.findViewById(R.id.detalle_factura_nombreCliente);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (datos.get(position).getEnviado() > 0) {
            Metodos.textViewAwesomeComponente(contexto, vh.iconoEnviado, contexto.getString(R.string.icono_enviado));
        } else {
            vh.iconoEnviado.setText("");
        }
        if (datos.get(position).getImpreso() > 0) {
            Metodos.textViewAwesomeComponente(contexto, vh.iconoImpreso, contexto.getString(R.string.icono_impreso));
        } else {
            vh.iconoImpreso.setText("");
        }
        vh.nombreCliente.setText(datos.get(position).getNombreComercialCliente());
        vh.numeroFactura.setText(datos.get(position).getNumeroFactura());
        vh.fechaFactura.setText(datos.get(position).getFechaFactura());
        vh.importeFactura.setText(Metodos.doubleToMoney(datos.get(position).getImporteFactura()));
        if (datos.get(position).getEstadoFactura() == 0) {
            vh.importePendiente.setText(Metodos.doubleToMoney(datos.get(position).getImportePendiente()));
        } else {
            vh.importePendiente.setText("");
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView numeroFactura;
        public TextView fechaFactura;
        public TextView importeFactura;
        public TextView importePendiente;
        public TextView iconoEnviado;
        public TextView iconoImpreso;
        public TextView nombreCliente;
    }
}