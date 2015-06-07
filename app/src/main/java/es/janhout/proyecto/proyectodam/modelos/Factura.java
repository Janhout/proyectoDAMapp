package es.janhout.proyecto.proyectodam.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.janhout.proyecto.proyectodam.util.Metodos;

public class Factura implements Parcelable, Serializable {

    private int cliente;
    private String nombreComercialCliente;
    private String numeroFactura;
    private Date fechaFactura;
    private int estadoFactura; // 0: impagada | 1: pagada | 2: borrador
    private float importeFactura;
    private float importePendiente;
    private int impreso; //0: no impresa | >1: impresa
    private int enviado; //0: no enviada | >1: enviada
    private int idFactura;

    public static final Creator<Factura> CREATOR = new Creator<Factura>() {
        @Override
        public Factura createFromParcel(Parcel parcel) {
            return new Factura(parcel);
        }
        @Override
        public Factura[] newArray(int i) {
            return new Factura[i];
        }
    };

    public Factura(Parcel parcel) {
        this.cliente = parcel.readInt();
        this.nombreComercialCliente = parcel.readString();
        this.numeroFactura = parcel.readString();
        this.fechaFactura = stringToDate(parcel.readString());
        this.estadoFactura = parcel.readInt();
        this.importeFactura = parcel.readFloat();
        this.importePendiente = parcel.readFloat();
        this.impreso = parcel.readInt();
        this.enviado = parcel.readInt();
        this.idFactura = parcel.readInt();
    }

    public Factura(){
    }

    public Factura(int cliente, String nombreComercialCliente, String numeroFactura, String fechaFactura, int estadoFactura, float importeFactura, float importePendiente, int impreso, int enviado, int idFactura) {
        this.cliente = cliente;
        this.nombreComercialCliente = nombreComercialCliente;
        this.numeroFactura = numeroFactura;
        this.fechaFactura = stringToDate(fechaFactura);
        this.estadoFactura = estadoFactura;
        this.importeFactura = importeFactura;
        this.importePendiente = importePendiente;
        this.impreso = impreso;
        this.enviado = enviado;
        this.idFactura = idFactura;
    }

    public Factura(JSONObject facturaJSON) {
        try {
            this.cliente = facturaJSON.getInt("cliente");
            this.nombreComercialCliente = facturaJSON.getString("nombre_comercial");
            this.numeroFactura = facturaJSON.getString("numero");
            String stringFecha = facturaJSON.getString("fecha");
            SimpleDateFormat toDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            this.fechaFactura = toDateFormatter.parse(stringFecha);
            String estadoFacturaTemp = facturaJSON.getString("estado");
            switch (estadoFacturaTemp) {
                case "impagada":
                    this.estadoFactura = 0;
                    break;
                case "pagada":
                    this.estadoFactura = 1;
                    break;
                case "borrador":
                    this.estadoFactura = 2;
                    break;
            }
            this.importeFactura = Metodos.stringToFloat(facturaJSON.getString("liquido"));
            this.importePendiente = Metodos.stringToFloat(facturaJSON.getString("pendiente"));
            this.impreso = facturaJSON.getInt("printed");
            this.enviado = facturaJSON.getInt("sent");
            this.idFactura = facturaJSON.getInt("id_s");
        } catch (JSONException | ParseException ignored) {
        }
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public String getNombreComercialCliente() {
        return nombreComercialCliente;
    }

    public void setNombreComercialCliente(String nombreComercialCliente) {
        this.nombreComercialCliente = nombreComercialCliente;
    }

    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getFechaFactura() {
        return dateToString();
    }

    public void setFechaFactura(String fechaFactura) {
        this.fechaFactura = stringToDate(fechaFactura);
    }

    public int getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(int estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public float getImporteFactura() {
        return importeFactura;
    }

    public void setImporteFactura(float importeFactura) {
        this.importeFactura = importeFactura;
    }

    public float getImportePendiente() {
        return importePendiente;
    }

    public void setImportePendiente(float importePendiente) {
        this.importePendiente = importePendiente;
    }

    public int getImpreso() {
        return impreso;
    }

    public void setImpreso(int impreso) {
        this.impreso = impreso;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado){
        this.enviado = enviado;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    private String dateToString(){
        SimpleDateFormat toStringFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return toStringFormatter.format(fechaFactura);
    }

    private Date stringToDate(String s){
        SimpleDateFormat toStringFormatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return toStringFormatter.parse(s);
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(cliente);
        parcel.writeString(nombreComercialCliente);
        parcel.writeString(numeroFactura);
        parcel.writeString(dateToString());
        parcel.writeInt(estadoFactura);
        parcel.writeFloat(importeFactura);
        parcel.writeFloat(importePendiente);
        parcel.writeInt(impreso);
        parcel.writeInt(enviado);
        parcel.writeInt(idFactura);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Factura factura = (Factura) o;

        if (cliente != factura.cliente) return false;
        if (enviado != factura.enviado) return false;
        if (estadoFactura != factura.estadoFactura) return false;
        if (idFactura != factura.idFactura) return false;
        if (Float.compare(factura.importeFactura, importeFactura) != 0) return false;
        if (Float.compare(factura.importePendiente, importePendiente) != 0) return false;
        if (impreso != factura.impreso) return false;
        if (fechaFactura != null ? !fechaFactura.equals(factura.fechaFactura) : factura.fechaFactura != null)
            return false;
        if (nombreComercialCliente != null ? !nombreComercialCliente.equals(factura.nombreComercialCliente) : factura.nombreComercialCliente != null)
            return false;
        if (!numeroFactura.equals(factura.numeroFactura)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cliente;
        result = 31 * result + (nombreComercialCliente != null ? nombreComercialCliente.hashCode() : 0);
        result = 31 * result + numeroFactura.hashCode();
        result = 31 * result + (fechaFactura != null ? fechaFactura.hashCode() : 0);
        result = 31 * result + estadoFactura;
        result = 31 * result + (importeFactura != +0.0f ? Float.floatToIntBits(importeFactura) : 0);
        result = 31 * result + (importePendiente != +0.0f ? Float.floatToIntBits(importePendiente) : 0);
        result = 31 * result + impreso;
        result = 31 * result + enviado;
        result = 31 * result + idFactura;
        return result;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "cliente=" + cliente +
                ", nombreComercialCliente='" + nombreComercialCliente + '\'' +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", fechaFactura=" + fechaFactura +
                ", estadoFactura=" + estadoFactura +
                ", importeFactura=" + importeFactura +
                ", importePendiente=" + importePendiente +
                ", impreso=" + impreso +
                ", enviado=" + enviado +
                ", idFactura=" + idFactura +
                '}';
    }
}
