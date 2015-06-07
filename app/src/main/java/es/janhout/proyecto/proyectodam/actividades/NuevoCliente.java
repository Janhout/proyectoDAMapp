package es.janhout.proyecto.proyectodam.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorAutoCompleteTextView;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.modelos.Localidad;
import es.janhout.proyecto.proyectodam.modelos.Provincia;
import es.janhout.proyecto.proyectodam.modelos.TipoDireccion;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class NuevoCliente extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener,
        AsyncTaskGet.OnProcessCompleteListener {

    private EditText inputNombreComercial;
    private EditText inputNIF;
    private EditText inputDireccion;
    private EditText inputNumero;
    private EditText inputPiso;
    private EditText inputCodigoPostal;
    private EditText inputEmail;
    private EditText inputTelefono1;
    private EditText inputTelefono2;

    private AutoCompleteTextView inputProvincia;
    private AutoCompleteTextView inputLocalidad;
    private AutoCompleteTextView inputTipoDireccion;

    private ArrayList<Object> listaProvincias;
    private ArrayList<Object> listaLocalidades;
    private ArrayList<Object> listaTiposDireccion;

    private String provinciaSeleccionada;

    private static final int CODIGO_COMPROBAR_CLIENTE = 1;
    private static final int CODIGO_NUEVO_CLIENTE = 2;
    private static final int CODIGO_GET_PROVINCIAS = 3;
    private static final int CODIGO_GET_LOCALIDADES = 4;
    private static final int CODIGO_GET_TIPOS_DIRECCION = 5;

    private static final String PARAMETRO_NIF = "nif";
    private static final String PARAMETRO_ID_PROVINCIA = "id_provincia";
    private static final String PARAMETRO_NOMBRE_COMERCIAL = "nombre_comercial";
    private static final String PARAMETRO_DIRECCION = "direccion";
    private static final String PARAMETRO_TELEFONO01 = "telefono01";
    private static final String PARAMETRO_TELEFONO02 = "teleforno02";
    private static final String PARAMETRO_EMAIL = "email";

   /* *************************************************************************
    **************************** Métodos on... ********************************
    *************************************************************************** */

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);
        inicializarToolbar();

        listaProvincias = new ArrayList<>();
        listaLocalidades = new ArrayList<>();
        listaTiposDireccion = new ArrayList<>();

        inicializarViews();

        AsyncTaskGet cargarProvincias = new AsyncTaskGet(this, this, Constantes.URL_GET_PROVINCIAS, false, CODIGO_GET_PROVINCIAS);
        cargarProvincias.execute(new Hashtable<String, String>());
        AsyncTaskGet cargarTiposDireccion = new AsyncTaskGet(this, this, Constantes.URL_GET_TIPO_DIRECCION, false, CODIGO_GET_TIPOS_DIRECCION);
        cargarTiposDireccion.execute(new Hashtable<String, String>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_cliente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_nuevoCliente_insertar) {
            nuevoCliente();
            return true;
        } else if (id == android.R.id.home) {
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
        provinciaSeleccionada = savedInstanceState.getString("Provincia");
        if (provinciaSeleccionada != null) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put(PARAMETRO_ID_PROVINCIA, provinciaSeleccionada);
            AsyncTaskGet cargarLocalidades = new AsyncTaskGet(NuevoCliente.this, NuevoCliente.this, Constantes.URL_GET_LOCALIDADES, false, CODIGO_GET_LOCALIDADES);
            cargarLocalidades.execute(parametros);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (provinciaSeleccionada != null) {
            outState.putString("Provincia", provinciaSeleccionada);
        }
    }

    /* *************************************************************************
    **************************** Auxiliares ************************************
    *************************************************************************** */

    private void cargarLocalidades(String respuesta){
        JSONTokener token = new JSONTokener(respuesta);
        JSONArray array;
        try {
            array = new JSONArray(token);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                listaLocalidades.add(new Localidad(obj));
            }
            AdaptadorAutoCompleteTextView adLocalidades = new AdaptadorAutoCompleteTextView(this, android.R.layout.simple_dropdown_item_1line, listaLocalidades);
            inputLocalidad.setAdapter(adLocalidades);
        } catch (JSONException e) {
            listaLocalidades.clear();
        }
    }

    private void cargarProvincias(String respuesta){
        JSONTokener token = new JSONTokener(respuesta);
        JSONArray array;
        try {
            array = new JSONArray(token);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                listaProvincias.add(new Provincia(obj));
            }
            AdaptadorAutoCompleteTextView adProvincias = new AdaptadorAutoCompleteTextView(this, android.R.layout.simple_dropdown_item_1line, listaProvincias);
            inputProvincia.setAdapter(adProvincias);
        } catch (JSONException e) {
            listaProvincias.clear();
        }
    }

    private void cargarTipoDireccion(String respuesta){
        JSONTokener token = new JSONTokener(respuesta);
        JSONArray array;
        try {
            array = new JSONArray(token);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                listaTiposDireccion.add(new TipoDireccion(obj));
            }
            AdaptadorAutoCompleteTextView adTiposDireccion = new AdaptadorAutoCompleteTextView(this, android.R.layout.simple_dropdown_item_1line, listaTiposDireccion);
            inputTipoDireccion.setAdapter(adTiposDireccion);
        } catch (JSONException e) {
            listaTiposDireccion.clear();
        }
    }

    private String formarDireccion(){
        StringBuilder direccion = new StringBuilder("");
        if(!inputTipoDireccion.getText().toString().trim().equals("")) {
            direccion.append(inputTipoDireccion.getText().toString().trim());
        }
        if(!inputDireccion.getText().toString().trim().equals("")) {
            direccion.append(" ".concat(inputDireccion.getText().toString().trim()));
        }
        if(!inputNumero.getText().toString().trim().equals("")) {
            direccion.append(", ".concat(inputNumero.getText().toString().trim()));
        }
        if(!inputPiso.getText().toString().trim().equals("")) {
            direccion.append(", ".concat(inputPiso.getText().toString().trim()));
        }
        if(!inputCodigoPostal.getText().toString().trim().equals("")) {
            direccion.append(", ".concat(inputCodigoPostal.getText().toString().trim()));
        }
        if(!inputLocalidad.getText().toString().trim().equals("")) {
            direccion.append(", ".concat(inputLocalidad.getText().toString().trim()));
        }
        if(!inputProvincia.getText().toString().trim().equals("")) {
            direccion.append(" ".concat(inputProvincia.getText().toString().trim()));
        }
        return direccion.toString();
    }

    private void inicializarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void inicializarViews(){
        inputNombreComercial = (EditText) findViewById(R.id.nuevoCliente_inputNombreComercial);
        inputNIF = (EditText) findViewById(R.id.nuevoCliente_NIF);
        inputDireccion = (EditText) findViewById(R.id.nuevoCliente_direccion);
        inputNumero = (EditText) findViewById(R.id.nuevoCliente_numero);
        inputPiso = (EditText) findViewById(R.id.nuevoCliente_piso);
        inputCodigoPostal = (EditText) findViewById(R.id.nuevoCliente_codigo_postal);
        inputEmail = (EditText) findViewById(R.id.nuevoCliente_email);
        inputTelefono1 = (EditText) findViewById(R.id.nuevoCliente_telefono1);
        inputTelefono2 = (EditText) findViewById(R.id.nuevoCliente_telefono2);
        inputProvincia = (AutoCompleteTextView) findViewById(R.id.nuevoCliente_provincia);
        inputLocalidad = (AutoCompleteTextView) findViewById(R.id.nuevoCliente_localidad);
        inputTipoDireccion = (AutoCompleteTextView) findViewById(R.id.nuevoCliente_tipoDireccion);

        inputProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listaLocalidades.clear();
                inputLocalidad.setText("");
                inputCodigoPostal.setText("");
                provinciaSeleccionada = ((Provincia) listaProvincias.get(i)).getIdProvincia();
                Hashtable<String, String> parametros = new Hashtable<>();
                parametros.put(PARAMETRO_ID_PROVINCIA, provinciaSeleccionada);
                AsyncTaskGet cargarLocalidades = new AsyncTaskGet(NuevoCliente.this, NuevoCliente.this, Constantes.URL_GET_LOCALIDADES, false, CODIGO_GET_LOCALIDADES);
                cargarLocalidades.execute(parametros);
            }
        });

        inputLocalidad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                inputCodigoPostal.setText(((Localidad) listaLocalidades.get(i)).getCpLocalidad());
            }
        });
    }

    private void insertarCliente(){
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_NOMBRE_COMERCIAL, inputNombreComercial.getText().toString());
        parametros.put(PARAMETRO_NIF, inputNIF.getText().toString());

        parametros.put(PARAMETRO_DIRECCION, formarDireccion());
        parametros.put(PARAMETRO_EMAIL, inputEmail.getText().toString());
        parametros.put(PARAMETRO_TELEFONO01, inputTelefono1.getText().toString());
        parametros.put(PARAMETRO_TELEFONO02, inputTelefono2.getText().toString());

        AsyncTaskPost nuevoCliente = new AsyncTaskPost(this, this, Constantes.URL_POST_CLIENTE, CODIGO_NUEVO_CLIENTE);
        nuevoCliente.execute(parametros);
    }

    private void nuevoCliente() {
        if (inputNombreComercial.getText().toString().trim().equals("")) {
            inputNombreComercial.requestFocus();
            Metodos.tostada(this, getString(R.string.e_nuevoCliente_nombre_vacio));
        } else if (inputNIF.getText().toString().trim().equals("")) {
            inputNIF.requestFocus();
            Metodos.tostada(this, getString(R.string.e_nuevoCliente_nif_vacio));
        } else if (!Metodos.validarNIF(inputNIF.getText().toString())) {
            inputNIF.requestFocus();
            Metodos.tostada(this, getString(R.string.e_nuevoCliente_nif_erroneo));
        } else {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put(PARAMETRO_NIF, inputNIF.getText().toString());
            parametros.put(PARAMETRO_NOMBRE_COMERCIAL, inputNombreComercial.getText().toString());
            AsyncTaskGet hebraComprobarCliente = new AsyncTaskGet(this, this, Constantes.URL_GET_COMPROBAR_CLIENTE, false, CODIGO_COMPROBAR_CLIENTE);
            hebraComprobarCliente.execute(parametros);
        }
    }

   /* *************************************************************************
    **************** Interfaz OnProcessCompleteListener ***********************
    *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo_peticion) {
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(this);
            } else {
                switch (codigo_peticion) {
                    case CODIGO_GET_LOCALIDADES:
                        cargarLocalidades(respuesta);
                        break;
                    case CODIGO_GET_PROVINCIAS:
                        cargarProvincias(respuesta);
                        break;
                    case CODIGO_GET_TIPOS_DIRECCION:
                        cargarTipoDireccion(respuesta);
                        break;
                    case CODIGO_COMPROBAR_CLIENTE:
                        if(respuesta.equals("r11")){
                            insertarCliente();
                        } else if(respuesta.equals("r15")){
                            Metodos.tostada(this, getString(R.string.e_nuevoCliente_existente));
                        } else {
                            Metodos.tostada(this, getString(R.string.e_parametros));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }

    @Override
    public void resultadoPost(String respuesta, int codigo_peticion) {
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(this);
            } else {
                switch (codigo_peticion) {
                    case CODIGO_NUEVO_CLIENTE:
                        if(!respuesta.contains("r")){
                            Metodos.tostada(this, getString(R.string.s_nuevoCliente_insertar_correcto));
                            Intent i = new Intent(this, MostrarCliente.class);
                            Bundle b = new Bundle();
                            Cliente cliente = new Cliente(Integer.parseInt(respuesta),
                                    inputNombreComercial.getText().toString(), inputNIF.getText().toString(),
                                    inputTelefono1.getText().toString(), inputTelefono2.getText().toString(),
                                    inputEmail.getText().toString());
                            cliente.setDireccion(formarDireccion());
                            b.putParcelable("cliente", cliente);
                            i.putExtras(b);

                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            Metodos.tostada(this, getString(R.string.e_nuevoCliente_error_insertar));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }
}