package es.janhout.proyecto.proyectodam.modelos;

import org.json.JSONException;
import org.json.JSONObject;

public class Localidad {

    private String idLocalidad;
    private String cpLocalidad;
    private String tituloLocalidad;
    private String localidad;

    public Localidad() {
    }

    public Localidad(JSONObject datos) {
        try {
            this.idLocalidad = datos.getString("id_localidad");
            this.cpLocalidad = datos.getString("codigo_postal");
            this.localidad = datos.getString("localidad");
            this.tituloLocalidad = datos.getString("titulo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(String idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public String getCpLocalidad() {
        return cpLocalidad;
    }

    public void setCpLocalidad(String cpLocalidad) {
        this.cpLocalidad = cpLocalidad;
    }

    public String getTituloLocalidad() {
        return tituloLocalidad;
    }

    public void setTituloLocalidad(String tituloLocalidad) {
        this.tituloLocalidad = tituloLocalidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}
