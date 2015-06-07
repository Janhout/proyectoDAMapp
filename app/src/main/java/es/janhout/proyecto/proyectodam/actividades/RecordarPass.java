package es.janhout.proyecto.proyectodam.actividades;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class RecordarPass extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener{

    private boolean mostrarDialogo;
    private Dialog dialogo;

    private final static String PARAMETRO_USUARIO = "usuario";

    private static final int CODIGO_RECUPERAR_PASS = 1;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordar_pass);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mostrarDialogo = savedInstanceState.getBoolean("mostrarDialogo");
        if(mostrarDialogo){
            mostrarDialogo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mostrarDialogo", mostrarDialogo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cerrarDialogo();
    }

    /* *************************************************************************
    **************************** Auxiliares ************************************
    *************************************************************************** */

    private void cerrarDialogo(){
        if(dialogo != null) {
            dialogo.dismiss();
        }
        mostrarDialogo = false;
    }

    private void mostrarDialogo(){
        dialogo = new Dialog(this, android.R.style.Theme_Panel);
        dialogo.setCancelable(false);
        mostrarDialogo = true;
        dialogo.show();
    }

    public void recuperar(View v){
        EditText etUsuario = (EditText)findViewById(R.id.etUsuario);
        String email = etUsuario.getText().toString();
        recuperarPass(email);
        mostrarDialogo();
    }

    private void recuperarPass(String usuario){
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_USUARIO, usuario);
        AsyncTaskPost a = new AsyncTaskPost(this, this, Constantes.URL_RECUPERAR_PASS, CODIGO_RECUPERAR_PASS);
        a.execute(parametros);
    }

    public void volverLogin(View v){
        this.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
    }

    /* *************************************************************************
    **************** Interfaz OnProcessCompleteListener ***********************
    *************************************************************************** */

    @Override
    public void resultadoPost(String respuesta, int codigo) {
        cerrarDialogo();
        if(respuesta != null) {
            switch (respuesta) {
                case "r11":
                    Metodos.tostada(this, getString(R.string.email_enviado));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                    break;
                case "r8":
                    Metodos.tostada(this, getString(R.string.e_usuario));
                    break;
                default:
                    Metodos.tostada(this, getString(R.string.e_parametros));
                    break;
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }
}