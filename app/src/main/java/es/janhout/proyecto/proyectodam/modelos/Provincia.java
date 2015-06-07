package es.janhout.proyecto.proyectodam.modelos;

import org.json.JSONException;
import org.json.JSONObject;

public class Provincia {
    private String idProvincia;
    private String provincia;
    private String tituloProvincia;

    public Provincia() {
    }

    public Provincia(String idProvincia, String tituloProvincia, String provincia) {
        this.idProvincia = idProvincia;
        this.tituloProvincia = tituloProvincia;
        this.provincia = provincia;
    }

    public Provincia(JSONObject datos) {
        try {
            this.idProvincia = datos.getString("id_provincia");
            this.provincia = datos.getString("provincia");
            this.tituloProvincia = datos.getString("titulo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(String idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTituloProvincia() {
        return tituloProvincia;
    }

    public void setTituloProvincia(String tituloProvincia) {
        this.tituloProvincia = tituloProvincia;
    }
}

