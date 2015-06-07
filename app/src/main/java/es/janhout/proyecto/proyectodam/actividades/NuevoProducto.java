package es.janhout.proyecto.proyectodam.actividades;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class NuevoProducto extends AppCompatActivity implements AsyncTaskPost.OnProcessCompleteListener,
        AsyncTaskGet.OnProcessCompleteListener{

    private EditText referencia;
    private EditText precio_compra;
    private EditText precio_venta;
    private EditText precio_impuestos;
    private EditText beneficio;
    private Spinner iva;
    private EditText descripcion;
    private EditText nombre;

    ArrayList<EditText> componentes;

    private double cImpuesto;
    private double cPrecioVenta;
    private double cPrecioCompra;
    private double cBeneficio;
    private double cPrecioImpuestos;

    private static final int CODIGO_COMPROBAR_REFERENCIA = 1;
    private static final int CODIGO_INSERTAR_PRODUCTO = 2;

    private static final String PARAMENTRO_NOMBRE = "nombre_producto";
    private static final String PARAMENTRO_REFERENCIA = "referencia_producto";
    private static final String PARAMENTRO_DESCRIPCION = "descripcion";
    private static final String PARAMENTRO_PRECIO_COSTE = "precio_coste";
    private static final String PARAMENTRO_PRECIO_VENTA = "precio_venta";
    private static final String PARAMENTRO_TIPO_IVA = "tipo_iva";

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
        setContentView(R.layout.activity_nuevo_producto);

        inicializarToolbar();
        inicializarViews();

        componentes = new ArrayList<>();
        componentes.add(nombre);
        componentes.add(referencia);
        componentes.add(precio_compra);
        componentes.add(precio_venta);
        componentes.add(precio_impuestos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_producto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_nuevo_producto) {
            nuevoProducto();
            return true;
        } else if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* *************************************************************************
     **************************** Auxiliares ***********************************
     *************************************************************************** */

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
        referencia = (EditText) findViewById(R.id.nuevoProducto_referencia);
        precio_compra = (EditText) findViewById(R.id.nuevoProducto_precio_compra);
        precio_venta = (EditText) findViewById(R.id.nuevoProducto_precio_venta);
        precio_impuestos = (EditText) findViewById(R.id.nuevoProducto_precio_impuestos);
        iva = (Spinner) findViewById(R.id.nuevoProducto_impuesto);
        beneficio = (EditText) findViewById(R.id.nuevoProducto_beneficio);
        descripcion = (EditText) findViewById(R.id.nuevoProducto_descripcion);
        nombre = (EditText) findViewById(R.id.nuevoProducto_nombre);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_iva, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iva.setAdapter(adapter);
        iva.setSelection(0);
        iva.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {
                    recalcularPrecios(view);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setListenerPrecios();
    }

    private void insertarProducto(){
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMENTRO_REFERENCIA, referencia.getText().toString());
        parametros.put(PARAMENTRO_NOMBRE, nombre.getText().toString());
        parametros.put(PARAMENTRO_DESCRIPCION, descripcion.getText().toString());
        parametros.put(PARAMENTRO_PRECIO_COSTE, precio_compra.getText().toString());
        parametros.put(PARAMENTRO_PRECIO_VENTA, precio_venta.getText().toString());
        parametros.put(PARAMENTRO_TIPO_IVA, iva.getSelectedItem().toString());

        AsyncTaskPost crearProducto = new AsyncTaskPost(this, this, Constantes.URL_POST_PRODUCTO, CODIGO_INSERTAR_PRODUCTO);
        crearProducto.execute(parametros);
    }

    public void nuevoProducto() {
        if (comprobarObligatorios()) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put(PARAMENTRO_REFERENCIA, referencia.getText().toString());
            AsyncTaskGet comprobarExistente = new AsyncTaskGet(this, this, Constantes.URL_GET_COMPROBAR_PRODUCTO, false, CODIGO_COMPROBAR_REFERENCIA);
            comprobarExistente.execute(parametros);
        }
    }

    private boolean comprobarObligatorios() {
        for (EditText componente : componentes) {
            if (componente.getText().toString().equals("")) {
                componente.requestFocus();
                Metodos.tostada(this, getString(R.string.e_no_vacio));
                return false;
            }
        }
        return true;
    }

    private void recalcularPrecios(View v) {
        cPrecioCompra = (Metodos.stringToDouble(precio_compra.getText().toString()));
        cImpuesto = (Metodos.stringToDouble(iva.getSelectedItem().toString())) / 100;
        if (v.equals(beneficio)) {
            cBeneficio = (Metodos.stringToDouble(beneficio.getText().toString()))/100;
            cPrecioVenta = cPrecioCompra + (cPrecioCompra * cBeneficio);
            cPrecioImpuestos = cPrecioVenta + (cPrecioVenta * cImpuesto);

            precio_venta.setText(Metodos.doubleToString(cPrecioVenta));
            precio_impuestos.setText(Metodos.doubleToString(cPrecioImpuestos));
        } else if (v.equals(precio_venta)) {
            cPrecioVenta = Metodos.stringToDouble(precio_venta.getText().toString());
            cPrecioImpuestos = cPrecioVenta + (cPrecioVenta * cImpuesto);
            cBeneficio = (100*(cPrecioVenta - cPrecioCompra))/cPrecioCompra;

            precio_impuestos.setText(Metodos.doubleToString(cPrecioImpuestos));
            beneficio.setText(Metodos.doubleToString(cBeneficio));
        } else if (v.equals(precio_impuestos)) {
            cPrecioImpuestos = Metodos.stringToDouble(precio_impuestos.getText().toString());
            cPrecioVenta = cPrecioImpuestos/(1 + cImpuesto);
            cBeneficio = (100*(cPrecioVenta - cPrecioCompra))/cPrecioCompra;

            precio_venta.setText(Metodos.doubleToString(cPrecioVenta));
            beneficio.setText(Metodos.doubleToString(cBeneficio));
        } else {
            cPrecioVenta = Metodos.stringToDouble(precio_venta.getText().toString());
            cPrecioImpuestos = cPrecioVenta + (cPrecioVenta * cImpuesto);
            precio_impuestos.setText(Metodos.doubleToString(cPrecioImpuestos));
        }
    }

    private void setListenerPrecios() {
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (view != null && !((EditText) view).getText().toString().equals(""))
                        recalcularPrecios(view);
                }
            }
        };
        precio_compra.setOnFocusChangeListener(focusChangeListener);
        beneficio.setOnFocusChangeListener(focusChangeListener);
        precio_venta.setOnFocusChangeListener(focusChangeListener);
        precio_impuestos.setOnFocusChangeListener(focusChangeListener);
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
                    case CODIGO_COMPROBAR_REFERENCIA:
                        if(respuesta.equals("r11")){
                            insertarProducto();
                        } else if(respuesta.equals("r16")){
                            Metodos.tostada(this, getString(R.string.e_nuevo_producto_referencia_existe));
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
                    case CODIGO_INSERTAR_PRODUCTO:
                        if(!respuesta.contains("r")){
                            Metodos.tostada(this, getString(R.string.s_nuevo_producto_succeed));
                            setResult(Activity.RESULT_OK);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                        } else {
                            Toast.makeText(this, R.string.s_nuevo_producto_e_insertar, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(this, getString(R.string.e_conexion));
        }
    }
}