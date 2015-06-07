package es.janhout.proyecto.proyectodam.actividades;

import android.app.Dialog;
import android.content.Intent;
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

public class Login extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener{

    private boolean mostrarDialogo;
    private Dialog dialogo;

    private static final String PARAMENTRO_USUARIO = "usuario";
    private static final String PARAMENTRO_PASS = "pass";
    private static final int CODIGO_LOGIN = 1;

    /* *************************************************************************
    **************************** Métodos on... ********************************
    *************************************************************************** */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Inicial.class));
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
    **************** Interfaz OnProcessCompleteListener ***********************
    *************************************************************************** */

    @Override
    public void resultadoPost(String respuesta, int codigo) {
        cerrarDialogo();
        if(respuesta != null) {
            switch (respuesta) {
                case "r1":
                    loginCorrecto();
                    break;
                case "r0":
                    Metodos.tostada(this, getString(R.string.e_login));
                    break;
                default:
                    Metodos.tostada(this, getString(R.string.e_parametros));
                    break;
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
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

    private void hacerLogin(String usuario, String pass){
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMENTRO_USUARIO, usuario);
        parametros.put(PARAMENTRO_PASS, pass);
        AsyncTaskPost a = new AsyncTaskPost(this, this, Constantes.URL_LOGIN, CODIGO_LOGIN);
        a.execute(parametros);
    }

    private void mostrarDialogo(){
        dialogo = new Dialog(this, android.R.style.Theme_Panel);
        dialogo.setCancelable(false);
        mostrarDialogo = true;
        dialogo.show();
    }

    public void login(View v){
        EditText etUsuario = (EditText)findViewById(R.id.etUsuario);
        EditText etPass = (EditText)findViewById(R.id.etPass);
        String usuario = etUsuario.getText().toString();
        String pass = etPass.getText().toString();

        if(!pass.trim().equals("") && !usuario.trim().equals("")) {
            hacerLogin(usuario, pass);
            mostrarDialogo();
        }
    }

    private void loginCorrecto(){
        Intent i = new Intent(this, Principal.class);
        i.putExtra("favorito", true);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        finish();
    }

    public void recuperaPass(View v){
        Intent i = new Intent(this, RecordarPass.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    public void volverInicio(View v){
        startActivity(new Intent(this, Inicial.class));
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
        finish();
    }
}