package es.janhout.proyecto.proyectodam.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aeat.valida.Validador;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.Login;

public class Metodos {

    private Metodos(){}

    /* *************************************************************************
     ********************** Métodos Shared Preferences *************************
     *************************************************************************** */

    public static void borrarPreferenciasCompartidas(Context contexto){
        SharedPreferences sharedPref = contexto.getSharedPreferences(contexto.getString(R.string.preferencias_compartidas), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    public static void escribirPreferenciasCompartidasInt(Context contexto, String key, int valor) {
        SharedPreferences sharedPref = contexto.getSharedPreferences(contexto.getString(R.string.preferencias_compartidas), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, valor);
        editor.apply();
    }

    public static void escribirPreferenciasCompartidasString(Context contexto, String key, String valor) {
        SharedPreferences sharedPref = contexto.getSharedPreferences(contexto.getString(R.string.preferencias_compartidas), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, valor);
        editor.apply();
    }

    public static int leerPreferenciasCompartidasInt(Context contexto, String key) {
        SharedPreferences sharedPref = contexto.getSharedPreferences(contexto.getString(R.string.preferencias_compartidas), Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }

    public static String leerPreferenciasCompartidasString(Context contexto, String key) {
        SharedPreferences sharedPref = contexto.getSharedPreferences(contexto.getString(R.string.preferencias_compartidas), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    /* *************************************************************************
     ***************************** Métodos FontAwesome *************************
     *************************************************************************** */

    public static void asignarFuente(Context contexto, int id, String fuente, int codigoComponente, String texto) {
        try {
            Typeface t = FontCache.get(fuente, contexto);
            switch (codigoComponente) {
                case 0:
                    Button b = (Button) ((Activity) contexto).findViewById(id);
                    b.setTypeface(t);
                    b.setText(texto);
                    break;
                case 1:
                    EditText e = (EditText) ((Activity) contexto).findViewById(id);
                    e.setTypeface(t);
                    e.setText(texto);
                    break;
                case 2:
                    TextView tv = (TextView) ((Activity) contexto).findViewById(id);
                    tv.setTypeface(t);
                    tv.setText(texto);
                    break;
            }
        }catch (Exception e){
            Log.v("error mio", "componente incorrecto");
        }
    }

    public static void asignarFuenteComponente(Context contexto, View v, String fuente, int codigoComponente, String texto) {
        try {
            Typeface t = FontCache.get(fuente, contexto);
            switch (codigoComponente) {
                case 0:
                    Button b = (Button) v;
                    b.setTypeface(t);
                    b.setText(texto);
                    break;
                case 1:
                    EditText e = (EditText) v;
                    e.setTypeface(t);
                    e.setText(texto);
                    break;
                case 2:
                    TextView tv = (TextView) v;
                    tv.setTypeface(t);
                    tv.setText(texto);
                    break;
            }
        }catch (Exception e){
            Log.v("error mio", "componente incorrecto");
        }
    }

    public static void botonAwesome(Context contexto, int id, String texto) {
        Metodos.asignarFuente(contexto, id, contexto.getString(R.string.fuente_awesome), 0, texto);
    }

    public static void botonAwesomeComponente(Context contexto, View v, String texto) {
        Metodos.asignarFuenteComponente(contexto, v, contexto.getString(R.string.fuente_awesome), 0, texto);
    }

    public static void textViewAwesome(Context contexto, int id, String texto) {
        Metodos.asignarFuente(contexto, id, contexto.getString(R.string.fuente_awesome), 2, texto);
    }

    public static void textViewAwesomeComponente(Context contexto, View v, String texto) {
        Metodos.asignarFuenteComponente(contexto, v, contexto.getString(R.string.fuente_awesome), 2, texto);
    }

    /* *************************************************************************
     *********************** Formatos String Numeros ***************************
     *************************************************************************** */

    public static String doubleToMoney(double valor){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(Metodos.redondear(valor, 2));
    }

    public static String doubleToString(double valor){
        return String.format(Locale.US, "%.2f", Metodos.redondear(valor, 2));
    }

    public static double redondear(double valor, int decimales) {
        try {
            if (decimales > -1){
                BigDecimal bd = new BigDecimal(valor);
                bd = bd.setScale(decimales, RoundingMode.HALF_UP);
                return bd.doubleValue();
            }
            return valor;
        } catch (NumberFormatException e){
            return 0;
        }
    }

    public static double stringToDouble(String valor){
        double d;
        try{
            d = Double.valueOf(valor);
        } catch (NumberFormatException e){
            d = 0;
        }
        return d;
    }

    public static float stringToFloat(String valor){
        float d;
        try{
            d = Float.valueOf(valor);
        } catch (NumberFormatException e){
            d = 0;
        }
        return d;
    }

    public static String stringToMoney(String valor){
        double d;
        try{
            d = Double.valueOf(valor);
        } catch (NumberFormatException e){
            d = 0;
        }
        return doubleToMoney(d);
    }

    /* *************************************************************************
     ************************************* Otros *******************************
     *************************************************************************** */

    public static boolean comprobarConexion(Context contexto) {
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static boolean comprobarIntenet() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException ignore) {
        }
        return false;
    }

    public static void redireccionarLogin(Context contexto){
        borrarPreferenciasCompartidas(contexto);
        Intent i = new Intent(contexto, Login.class);
        Toast.makeText(contexto, contexto.getResources().getString(R.string.e_sesion_expirada), Toast.LENGTH_SHORT).show();
        contexto.startActivity(i);
        ((Activity)contexto).finish();
    }

    public static void tostada(Context contexto, String texto){
        Toast.makeText(contexto, texto, Toast.LENGTH_SHORT).show();
    }

    public static boolean validarNIF(String nif){
        String n = nif.toUpperCase();
        Validador val = new Validador();
        return val.checkNif(n.trim())>0;
    }
}