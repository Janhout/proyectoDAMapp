package es.janhout.proyecto.proyectodam.modelos;

import org.json.JSONException;
import org.json.JSONObject;

public class TipoDireccion {

    private String idTipoDireccion;
    private String tipoDireccion;
    private String tituloTipoDireccion;

    public TipoDireccion() {
    }

    public TipoDireccion(JSONObject datos) {
        try {
            this.tipoDireccion = datos.getString("tipo");
            this.idTipoDireccion = datos.getString("id_tipo_direccion");
            this.tituloTipoDireccion = datos.getString("titulo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIdTipoDireccion() {
        return idTipoDireccion;
    }

    public void setIdTipoDireccion(String idTipoDireccion) {
        this.idTipoDireccion = idTipoDireccion;
    }

    public String getTipoDireccion() {
        return tipoDireccion;
    }

    public void setTipoDireccion(String tipoDireccion) {
        this.tipoDireccion = tipoDireccion;
    }

    public String getTituloTipoDireccion() {
        return tituloTipoDireccion;
    }

    public void setTituloTipoDireccion(String tituloTipoDireccion) {
        this.tituloTipoDireccion = tituloTipoDireccion;
    }
}
