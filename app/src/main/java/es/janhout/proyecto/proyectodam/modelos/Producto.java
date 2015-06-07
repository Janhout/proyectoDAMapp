package es.janhout.proyecto.proyectodam.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import es.janhout.proyecto.proyectodam.util.Metodos;

public class Producto implements Parcelable, Serializable {

    private int id_a;
    private String nombre_producto;
    private String referencia_producto;
    private String descripcion;
    private String precio_coste;
    private String precio_venta;
    private String tipo_iva;
    private String cantidad;

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel parcel) {
            return new Producto(parcel);
        }
        @Override
        public Producto[] newArray(int i) {
            return new Producto[i];
        }
    };

    public Producto(Parcel parcel) {
        this.id_a = parcel.readInt();
        this.nombre_producto = parcel.readString();
        this.referencia_producto = parcel.readString();
        this.descripcion = parcel.readString();
        this.precio_coste = parcel.readString();
        this.precio_venta = parcel.readString();
        this.tipo_iva = parcel.readString();
        this.cantidad = parcel.readString();
    }

    public Producto(){
    }

    public Producto(JSONObject obj){
        try {
            this.id_a = obj.getInt("id_producto");
            this.nombre_producto = obj.getString("nombre_producto");
            this.referencia_producto = obj.getString("referencia_producto");
            this.descripcion = obj.getString("descripcion");
            this.precio_coste = Metodos.doubleToString(obj.getDouble("precio_coste"));
            this.precio_venta = Metodos.doubleToString(obj.getDouble("precio_venta"));
            this.tipo_iva = String.valueOf(obj.getInt("tipo_iva"));
            this.cantidad = "1";
        } catch (JSONException ignore) {
        }
    }

    public Producto(int id_a, String nombre_producto, String referencia_producto, String descripcion,
                    String precio_coste, String precio_venta, String tipo_iva, String cantidad) {
        this.id_a = id_a;
        this.nombre_producto = nombre_producto;
        this.referencia_producto = referencia_producto;
        this.descripcion = descripcion;
        this.precio_coste = precio_coste;
        this.precio_venta = precio_venta;
        this.tipo_iva = tipo_iva;
        this.cantidad = cantidad;
    }

    public Producto clonar(){
        Producto producto = new Producto();
        producto.setCantidad(cantidad);
        producto.setNombre_producto(nombre_producto);
        producto.setReferencia_producto(referencia_producto);
        producto.setPrecio_coste(precio_coste);
        producto.setId_a(id_a);
        producto.setPrecio_venta(precio_venta);
        producto.setTipo_iva(tipo_iva);
        producto.setDescripcion(descripcion);
        return producto;
    }

    public int getId_a() {
        return id_a;
    }

    public void setId_a(int id_a) {
        this.id_a = id_a;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getReferencia_producto() {
        return referencia_producto;
    }

    public void setReferencia_producto(String referencia_producto) {
        this.referencia_producto = referencia_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecio_coste() {
        return precio_coste;
    }

    public void setPrecio_coste(String precio_coste) {
        this.precio_coste = precio_coste;
    }

    public String getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(String precio_venta) {
        this.precio_venta = precio_venta;
    }

    public String getTipo_iva() {
        return tipo_iva;
    }

    public void setTipo_iva(String tipo_iva) {
        this.tipo_iva = tipo_iva;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id_a);
        parcel.writeString(nombre_producto);
        parcel.writeString(referencia_producto);
        parcel.writeString(descripcion);
        parcel.writeString(precio_coste);
        parcel.writeString(precio_venta);
        parcel.writeString(tipo_iva);
        parcel.writeString(cantidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Producto producto = (Producto) o;

        if (id_a != producto.id_a) return false;
        if (cantidad != null ? !cantidad.equals(producto.cantidad) : producto.cantidad != null)
            return false;
        if (descripcion != null ? !descripcion.equals(producto.descripcion) : producto.descripcion != null)
            return false;
        if (!nombre_producto.equals(producto.nombre_producto)) return false;
        if (!precio_coste.equals(producto.precio_coste)) return false;
        if (!precio_venta.equals(producto.precio_venta)) return false;
        if (!referencia_producto.equals(producto.referencia_producto)) return false;
        if (!tipo_iva.equals(producto.tipo_iva)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id_a;
        result = 31 * result + nombre_producto.hashCode();
        result = 31 * result + referencia_producto.hashCode();
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + precio_coste.hashCode();
        result = 31 * result + precio_venta.hashCode();
        result = 31 * result + tipo_iva.hashCode();
        result = 31 * result + (cantidad != null ? cantidad.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id_a=" + id_a +
                ", nombre_producto='" + nombre_producto + '\'' +
                ", referencia_producto='" + referencia_producto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio_coste='" + precio_coste + '\'' +
                ", precio_venta='" + precio_venta + '\'' +
                ", tipo_iva='" + tipo_iva + '\'' +
                ", cantidad='" + cantidad + '\'' +
                '}';
    }
}