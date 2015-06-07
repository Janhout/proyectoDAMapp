package es.janhout.proyecto.proyectodam.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class Inicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String cookie = Metodos.leerPreferenciasCompartidasString(this, "cookieSesion");
        if (cookie.equals("")){
            setContentView(R.layout.activity_inicial);
        } else {
            loginCorrecto();
        }
    }

    public void login(View v){
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        finish();
    }

    private void loginCorrecto(){
        Intent i = new Intent(this, Principal.class);
        i.putExtra("favorito", true);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        finish();
    }

    public void nuevoUsuario(View v){
        startActivity(new Intent(this, CrearCuenta.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        finish();
    }
}
