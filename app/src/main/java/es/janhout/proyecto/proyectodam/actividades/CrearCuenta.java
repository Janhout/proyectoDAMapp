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
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class CrearCuenta extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener,
        AsyncTaskGet.OnProcessCompleteListener{

    private EditText usuario;
    private EditText empresa;
    private EditText email;
    private EditText nif;
    private EditText pass;
    private EditText repPass;

    private boolean mostrarDialogo;
    private Dialog dialogo;

    private static final int CODIGO_COMPROBAR_USUARIO = 1;
    private static final int CODIGO_INSERTAR_USUARIO = 2;

    private static final String PARAMETRO_USUARIO = "usuario";
    private static final String PARAMETRO_EMPRESA = "nombre_empresa";
    private static final String PARAMETRO_EMAIL = "email";
    private static final String PARAMETRO_NIF = "nif";
    private static final String PARAMETRO_PASS = "password";

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
        setContentView(R.layout.activity_crear_cuenta);
        inicializarViews();
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

    private boolean comprobarVacios(){
        boolean correcto = true;
        if(empresa.getText().toString().trim().equals("")){
            empresa.requestFocus();
            correcto = false;
        } else if(nif.getText().toString().trim().equals("")){
            nif.requestFocus();
            correcto = false;
        }else if(email.getText().toString().trim().equals("")){
            email.requestFocus();
            correcto = false;
        } else if(usuario.getText().toString().trim().equals("")){
            usuario.requestFocus();
            correcto = false;
        } else if(pass.getText().toString().trim().equals("")){
            pass.requestFocus();
            correcto = false;
        } else if(usuario.getText().toString().trim().equals("")){
            repPass.requestFocus();
            correcto = false;
        }
        if(!correcto){
            Metodos.tostada(this, getString(R.string.e_no_vacio));
        }
        return correcto;
    }

    public void creaCuenta(View v){
        if(comprobarVacios() && validarCampos()) {
            mostrarDialogo();
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put(PARAMETRO_USUARIO, usuario.getText().toString());
            parametros.put(PARAMETRO_EMPRESA, empresa.getText().toString());
            AsyncTaskGet h = new AsyncTaskGet(this, this, Constantes.URL_GET_COMPROBAR_USUARIO, false, CODIGO_COMPROBAR_USUARIO);
            h.execute(parametros);
        }
    }

    private void inicializarViews(){
        usuario = (EditText)findViewById(R.id.crear_etUsuario);
        repPass = (EditText)findViewById(R.id.crear_etRepetirPass);
        pass = (EditText)findViewById(R.id.crear_etPass);
        nif = (EditText)findViewById(R.id.crear_etNif);
        empresa = (EditText)findViewById(R.id.crear_etEmpresa);
        email = (EditText)findViewById(R.id.crear_etEmail);
    }

    private void insertarUsuario(){
        mostrarDialogo();
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_USUARIO, usuario.getText().toString().trim());
        parametros.put(PARAMETRO_EMPRESA, empresa.getText().toString().trim());
        parametros.put(PARAMETRO_EMAIL, email.getText().toString().trim());
        parametros.put(PARAMETRO_PASS, pass.getText().toString().trim());
        parametros.put(PARAMETRO_NIF, nif.getText().toString().trim());
        AsyncTaskPost h = new AsyncTaskPost(this, this, Constantes.URL_POST_USUARIO, CODIGO_INSERTAR_USUARIO);
        h.execute(parametros);
    }

    private void irLogin(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        finish();
    }

    private void mostrarDialogo(){
        dialogo = new Dialog(this, android.R.style.Theme_Panel);
        dialogo.setCancelable(false);
        mostrarDialogo = true;
        dialogo.show();
    }

    private boolean validarCampos(){
        boolean correcto = true;
        if(!Metodos.validarNIF(nif.getText().toString())) {
            nif.requestFocus();
            Metodos.tostada(this, getString(R.string.e_nuevoCliente_nif_erroneo));
            correcto = false;
        } else if(!pass.getText().toString().equals(repPass.getText().toString())){
            repPass.setText("");
            pass.setText("");
            pass.requestFocus();
            Metodos.tostada(this, getString(R.string.crear_cuenta_e_pass));
            correcto = false;
        }
        return correcto;
    }

    public void volverInicio(View v){
        startActivity(new Intent(this, Inicial.class));
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
        finish();
    }

    /* *************************************************************************
    **************** Interfaz OnProcessCompleteListener ***********************
    *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo_peticion) {
        cerrarDialogo();
        if(respuesta != null) {
            switch (codigo_peticion) {
                case CODIGO_COMPROBAR_USUARIO:
                    if(respuesta.equals("r11")){
                        insertarUsuario();
                    } else if(respuesta.equals("r15")){
                        Metodos.tostada(this, getString(R.string.crear_cuenta_existente));
                    } else {
                        Metodos.tostada(this, getString(R.string.e_parametros));
                    }
                    break;
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }

    @Override
    public void resultadoPost(String respuesta, int codigo) {
        cerrarDialogo();
        if(respuesta != null) {
            switch (codigo) {
                case CODIGO_INSERTAR_USUARIO:
                    if(!respuesta.contains("r")){
                        Metodos.tostada(this, getString(R.string.crear_cuenta_alta_correcta));
                        irLogin();
                    } else {
                        Metodos.tostada(this, getString(R.string.crear_cuenta_e_insertar));
                    }
                    break;
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }
}