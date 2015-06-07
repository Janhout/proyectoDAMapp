package es.janhout.proyecto.proyectodam.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;

public class AsyncTaskPost extends AsyncTask<Hashtable<String, String>, Void, String>{

    private Context contexto;
    private String url;
    private OnProcessCompleteListener listener;
    private LinearLayout layoutProgreso;
    private int codigo_peticion;
    private String fichero;

    public AsyncTaskPost(Context contexto, OnProcessCompleteListener listener, String url, int codigo_peticion){
        this.contexto = contexto;
        this.url = url;
        this.listener = listener;
        this.layoutProgreso = ((LinearLayout)((AppCompatActivity)contexto).findViewById(R.id.dialogo_progreso));
        this.codigo_peticion = codigo_peticion;
    }

    public AsyncTaskPost(Context contexto, OnProcessCompleteListener listener, String url, String fichero, int codigo_peticion){
        this.contexto = contexto;
        this.url = url;
        this.fichero = fichero;
        this.listener = listener;
        this.layoutProgreso = ((LinearLayout)((AppCompatActivity)contexto).findViewById(R.id.dialogo_progreso));
        this.codigo_peticion = codigo_peticion;
    }

    @Override
    protected void onPreExecute() {
        layoutProgreso.bringToFront();
        layoutProgreso.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Hashtable<String, String>[] params) {
        if(fichero != null) {
            return Peticiones.peticionPostFile(contexto, url, fichero);
        } else{
            return Peticiones.peticionPostJSON(contexto, url, params[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        layoutProgreso.setVisibility(View.GONE);
        listener.resultadoPost(s, codigo_peticion);
    }

    public interface OnProcessCompleteListener {
        public void resultadoPost(String respuesta, int codigo_peticion);
    }
}