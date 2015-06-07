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

public class Gasto implements Serializable, Parcelable{

    private int id;
    private String rutaFoto;
    private String concepto;
    private String importe;
    private String iva;
    private Date fecha;
    private String detalles;

    public static final Creator<Gasto> CREATOR = new Creator<Gasto>() {
        @Override
        public Gasto createFromParcel(Parcel parcel) {
            return new Gasto(parcel);
        }
        @Override
        public Gasto[] newArray(int i) {
            return new Gasto[i];
        }
    };

    public Gasto(Parcel parcel) {
        this.id = parcel.readInt();
        this.rutaFoto = parcel.readString();
        this.concepto = parcel.readString();
        this.importe = parcel.readString();
        this.iva = parcel.readString();
        this.fecha = stringToDate(parcel.readString());
        this.detalles = parcel.readString();
    }

    public Gasto() {
    }

    public Gasto(JSONObject obj){
        try {
            this.id = obj.getInt("id_gasto");
            this.rutaFoto = obj.getString("foto");
            this.concepto = obj.getString("concepto");
            this.detalles = obj.getString("detalles");
            this.importe = Metodos.doubleToString(obj.getDouble("importe"));
            this.iva = Metodos.doubleToString(obj.getDouble("tipo_iva"));
            String stringFecha = obj.getString("fecha");
            SimpleDateFormat toDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            this.fecha = toDateFormatter.parse(stringFecha);
        } catch (JSONException | ParseException ignore) {
        }
    }

    private String dateToString(){
        SimpleDateFormat toStringFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return toStringFormatter.format(fecha);
    }

    private Date stringToDate(String s){
        SimpleDateFormat toStringFormatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return toStringFormatter.parse(s);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getFecha() {
        return dateToString();
    }

    public void setFecha(String fecha) {
        this.fecha = stringToDate(fecha);
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(rutaFoto);
        parcel.writeString(concepto);
        parcel.writeString(importe);
        parcel.writeString(iva);
        parcel.writeString(dateToString());
        parcel.writeString(detalles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gasto gasto = (Gasto) o;

        if (id != gasto.id) return false;
        if (!concepto.equals(gasto.concepto)) return false;
        if (detalles != null ? !detalles.equals(gasto.detalles) : gasto.detalles != null)
            return false;
        if (!fecha.equals(gasto.fecha)) return false;
        if (!importe.equals(gasto.importe)) return false;
        if (iva != null ? !iva.equals(gasto.iva) : gasto.iva != null) return false;
        if (rutaFoto != null ? !rutaFoto.equals(gasto.rutaFoto) : gasto.rutaFoto != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (rutaFoto != null ? rutaFoto.hashCode() : 0);
        result = 31 * result + concepto.hashCode();
        result = 31 * result + importe.hashCode();
        result = 31 * result + (iva != null ? iva.hashCode() : 0);
        result = 31 * result + fecha.hashCode();
        result = 31 * result + (detalles != null ? detalles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Gasto{" +
                "id=" + id +
                ", rutaFoto='" + rutaFoto + '\'' +
                ", concepto='" + concepto + '\'' +
                ", importe='" + importe + '\'' +
                ", iva='" + iva + '\'' +
                ", fecha=" + fecha +
                ", detalles='" + detalles + '\'' +
                '}';
    }
}