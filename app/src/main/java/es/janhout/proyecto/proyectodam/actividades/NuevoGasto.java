package es.janhout.proyecto.proyectodam.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class NuevoGasto extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener{

    private ImageView fotoGasto;
    private EditText concepto;
    private Spinner tipo_iva;
    private EditText detalles;
    private EditText importe;

    private Button borrar_foto;

    private String foto;

    private Dialog dialogo;
    private boolean mostrarDialogo;

    private static final int ACTIVIDAD_CAMARA = 0;
    private static final int CODIGO_INSERTAR_GASTO = 1;
    private static final int CODIGO_INSERTAR_FOTO = 2;

    private static final String PARAMETRO_IVA = "tipo_iva";
    private static final String PARAMETRO_CONCEPTO = "concepto";
    private static final String PARAMETRO_DETALLES = "detalles";
    private static final String PARAMETRO_IMPORTE = "importe";

    /* *************************************************************************
    **************************** Métodos on... ********************************
    *************************************************************************** */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVIDAD_CAMARA:
                    fotoGasto.setImageBitmap(BitmapFactory.decodeFile(foto));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        borrarFicheroTmp();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gasto);
        inicializarToolbar();
        inicializarViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_gasto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_nueva_foto){
            nuevaFoto();
            return true;
        } else if(id == R.id.action_nuevo_gasto){
            guardarGasto();
            return true;
        } else if (id == android.R.id.home) {
            borrarFicheroTmp();
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void borrarFicheroTmp(){
        if(foto != null) {
            File f = new File(foto);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    public void borrarFoto(View v){
        fotoGasto.setImageDrawable(null);
        foto = null;
    }

    private void cerrarActividadGastoInsertado(){
        Metodos.tostada(this, getString(R.string.s_nuevoGasto_insertado));
        borrarFicheroTmp();
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
    }

    public void cerrarDialogo(){
        if(dialogo != null) {
            dialogo.dismiss();
        }
        mostrarDialogo = false;
    }

    private void guardarGasto(){
        mostrarDialogo();
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_CONCEPTO, concepto.getText().toString().trim());
        parametros.put(PARAMETRO_IVA, tipo_iva.getSelectedItem().toString().trim());
        parametros.put(PARAMETRO_DETALLES, detalles.getText().toString().trim());
        parametros.put(PARAMETRO_IMPORTE, importe.getText().toString().trim());

        AsyncTaskPost h = new AsyncTaskPost(this, this, Constantes.URL_POST_GASTO, CODIGO_INSERTAR_GASTO);
        h.execute(parametros);
    }

    private void inicializarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void inicializarViews(){
        fotoGasto = (ImageView) findViewById(R.id.nuevoGasto_imagen);
        concepto = (EditText) findViewById(R.id.nuevoGasto_concepto);
        detalles = (EditText) findViewById(R.id.nuevoGasto_detalles);
        importe = (EditText) findViewById(R.id.nuevoGasto_importe);
        tipo_iva = (Spinner) findViewById(R.id.nuevoGasto_tipo_iva);
        borrar_foto = (Button) findViewById(R.id.bt_borrar_foto);

        Metodos.botonAwesomeComponente(this, borrar_foto, getString(R.string.icono_equis));

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_iva, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo_iva.setAdapter(adapter);
        tipo_iva.setSelection(0);
    }

    private void insertarFoto(String nombre) {
        if (foto != null){
            File origen = new File(foto);
            if (origen.exists()) {
                mostrarDialogo();
                File fin = new File(getExternalFilesDir("") + "/" + nombre + ".jpg");
                origen.renameTo(fin);
                foto = fin.getAbsolutePath();

                AsyncTaskPost h = new AsyncTaskPost(this, this, Constantes.URL_POST_GASTO_FOTO, fin.getAbsolutePath(), CODIGO_INSERTAR_FOTO);
                h.execute();
            }
        } else {
            cerrarActividadGastoInsertado();
        }
    }

    public void mostrarDialogo(){
        dialogo = new Dialog(this, android.R.style.Theme_Panel);
        dialogo.setCancelable(false);
        mostrarDialogo = true;
        dialogo.show();
    }

    private void nuevaFoto(){
        Intent tomaFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomaFoto.resolveActivity(getPackageManager()) != null) {
            foto = getExternalFilesDir("") + "/img.tmp";
            File fichero = new File(foto);
            tomaFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero));
            startActivityForResult(tomaFoto, ACTIVIDAD_CAMARA);
        } else {
            Metodos.tostada(this, getString(R.string.no_app_disponible));
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoPost(String respuesta, int codigo_peticion) {
        cerrarDialogo();
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(this);
            } else {
                switch (codigo_peticion) {
                    case CODIGO_INSERTAR_GASTO:
                        if(!respuesta.contains("r")){
                            insertarFoto(respuesta);
                        } else {
                            Metodos.tostada(this, getString(R.string.s_nuevoGasto_error_insertar));
                        }
                        break;
                    case CODIGO_INSERTAR_FOTO:
                        if(respuesta.equals("r11")){
                            cerrarActividadGastoInsertado();
                        } else {
                            Metodos.tostada(this, getString(R.string.s_nuevoGasto_error_insertar_foto));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }
}