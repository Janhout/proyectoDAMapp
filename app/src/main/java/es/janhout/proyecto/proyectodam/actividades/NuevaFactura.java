package es.janhout.proyecto.proyectodam.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoListaProductos;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoNuevaFactura;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoNuevaLinea;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoSeleccionarCliente;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.modelos.Producto;

public class NuevaFactura extends AppCompatActivityBusqueda implements FragmentoSeleccionarCliente.OnClienteSelectedListener,
        FragmentoListaProductos.OnProductoListaSelectedListener, FragmentoNuevaLinea.OnProductoSelectedListener{

    private FragmentoNuevaFactura fragmentoPrincipal;
    private FragmentoNuevaLinea fragmentoNuevaLinea;
    private String tituloActividad;
    private SearchView searchView;
    private boolean inicio;
    private Cliente clienteFactura;
    private Dialog dialogo;
    private boolean mostrarDialogo;

    private final static String TAG_FRAGMENTO_PRINCIPAL = "fragmento_principal";
    private final static String TAG_FRAGMENTO_NUEVO_PRODUCTO = "fragmento_nuevo_producto";

    private final static int ACTIVIDAD_NUEVO_PRODUCTO = 1;
    private final static int ACTIVIDAD_NUEVO_CLIENTE = 2;

    public static enum ListaFragmentosNuevaFactura {
        ninguno,
        seleccionCliente,
        nuevaFactura,
        seleccionarProducto,
        nuevaLinea
    }

    public static ListaFragmentosNuevaFactura fragmentoActual;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVIDAD_NUEVO_PRODUCTO:
                    fragmentoActual = ListaFragmentosNuevaFactura.seleccionarProducto;
                    break;
                case ACTIVIDAD_NUEVO_CLIENTE:
                    fragmentoActual = ListaFragmentosNuevaFactura.seleccionCliente;
                    break;
            }
            busqueda("");
        }
    }

    @Override
    public void onBackPressed() {
        switch (fragmentoActual){
            case ninguno:
            case nuevaFactura:
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                break;
            case seleccionarProducto:
                FragmentoNuevaFactura.productoModificar = -1;
                mostrarFragmentoNuevaLinea(false, null);
                break;
            case seleccionCliente:
            case nuevaLinea:
                mostrarFragmentoNuevaFactura();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_factura);
        inicio = true;
        if(savedInstanceState != null) {
            inicio = savedInstanceState.getBoolean("ini");
            clienteFactura = savedInstanceState.getParcelable("clienteFactura");
        }
        inicializarToolbar();
        if(inicio) {
            Bundle b = getIntent().getExtras();
            if(b != null) {
                clienteFactura = b.getParcelable("cliente");
            }
            mostrarFragmentoNuevaFactura();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nueva_factura, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) {
            searchView.setIconified(false);
            return true;
        } else if (id == android.R.id.home) {
            if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionCliente ||
                    fragmentoActual == ListaFragmentosNuevaFactura.nuevaLinea){
                mostrarFragmentoNuevaFactura();
            } else if (fragmentoActual == ListaFragmentosNuevaFactura.nuevaFactura ||
                    fragmentoActual == ListaFragmentosNuevaFactura.ninguno){
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
            } else if (fragmentoActual == ListaFragmentosNuevaFactura.seleccionarProducto){
                FragmentoNuevaFactura.productoModificar = -1;
                mostrarFragmentoNuevaLinea(false, null);
            }
            invalidateOptionsMenu();
            return true;
        } else if(id == R.id.action_guardar_factura){
            if(fragmentoActual == ListaFragmentosNuevaFactura.nuevaFactura){
                return false;
            }
        } else if(id == R.id.action_nuevoProducto){
            startActivityForResult(new Intent(this, NuevoProducto.class), ACTIVIDAD_NUEVO_PRODUCTO);
            return true;
        } else if(id == R.id.action_nuevoCliente){
            Intent i = new Intent(this, NuevoCliente.class);
            Bundle b = new Bundle();
            b.putBoolean("result", true);
            i.putExtras(b);
            startActivityForResult(i, ACTIVIDAD_NUEVO_CLIENTE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionCliente ||
                fragmentoActual == ListaFragmentosNuevaFactura.seleccionarProducto) {
            menu.findItem(R.id.action_search).setVisible(true);
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionarProducto){
            searchView.setQueryHint(getString(R.string.hint_busqueda_producto));
            menu.findItem(R.id.action_nuevoProducto).setVisible(true);
        } else {
            menu.findItem(R.id.action_nuevoProducto).setVisible(false);
        }
        if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionCliente){
            searchView.setQueryHint(getString(R.string.hint_busqueda_cliente));
            menu.findItem(R.id.action_nuevoCliente).setVisible(true);
        } else {
            menu.findItem(R.id.action_nuevoCliente).setVisible(false);
        }
        if(fragmentoActual == ListaFragmentosNuevaFactura.nuevaFactura ||
                fragmentoActual == ListaFragmentosNuevaFactura.nuevaLinea) {
            menu.findItem(R.id.action_guardar_factura).setVisible(true);
        }else{
            menu.findItem(R.id.action_guardar_factura).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragmentoActual = (ListaFragmentosNuevaFactura)savedInstanceState.getSerializable("fragmentoActual");
        mostrarDialogo = savedInstanceState.getBoolean("mostrarDialogo");
        if(mostrarDialogo){
            mostrarDialogo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ini", inicio);
        outState.putParcelable("clienteFactura", clienteFactura);
        outState.putSerializable("fragmentoActual", fragmentoActual);
        outState.putBoolean("mostrarDialogo", mostrarDialogo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cerrarDialogo();
    }

    /* *************************************************************************
     ************************ Override Busqueda ********************************
     *************************************************************************** */

    @Override
    protected void busqueda(String textoBusqueda) {
        Fragment f = null;
        if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionCliente){
            f = fragmentoClientes(textoBusqueda);
        } else if(fragmentoActual == ListaFragmentosNuevaFactura.seleccionarProducto){
            f = fragmentoProductos(textoBusqueda);
        }
        if (f != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutFactura, f).commit();
        }
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    public void cerrarDialogo(){
        if(dialogo != null) {
            dialogo.dismiss();
        }
        mostrarDialogo = false;
    }

    private Fragment fragmentoClientes(String query){
        Fragment fragment = new FragmentoSeleccionarCliente();
        fragmentoActual = ListaFragmentosNuevaFactura.seleccionCliente;
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        return fragment;
    }

    private Fragment fragmentoProductos(String query){
        Fragment fragment = new FragmentoListaProductos();
        fragmentoActual = ListaFragmentosNuevaFactura.seleccionarProducto;
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        bundle.putBoolean("listener", true);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Cliente getClienteFactura(){
        return clienteFactura;
    }

    private void inicializarToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fragmentoActual = ListaFragmentosNuevaFactura.ninguno;
        if(tituloActividad == null) {
            tituloActividad = getTitle().toString();
        }
    }

    public void mostrarDialogo(){
        dialogo = new Dialog(this, android.R.style.Theme_Panel);
        dialogo.setCancelable(false);
        mostrarDialogo = true;
        dialogo.show();
    }

    private void mostrarFragmentoNuevaFactura(){
        FragmentManager fm = getSupportFragmentManager();
        fragmentoPrincipal = (FragmentoNuevaFactura)fm.findFragmentByTag(TAG_FRAGMENTO_PRINCIPAL);
        if(fragmentoPrincipal == null){
            fragmentoPrincipal = new FragmentoNuevaFactura();
            FragmentTransaction ft = fm.beginTransaction().add(fragmentoPrincipal, TAG_FRAGMENTO_PRINCIPAL);
            ft.addToBackStack(TAG_FRAGMENTO_PRINCIPAL);
            ft.commit();
            fm.executePendingTransactions();
        }
        fragmentoActual = ListaFragmentosNuevaFactura.nuevaFactura;
        invalidateOptionsMenu();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutFactura, fragmentoPrincipal, TAG_FRAGMENTO_PRINCIPAL);
        ft.addToBackStack(TAG_FRAGMENTO_PRINCIPAL);
        ft.commit();
        fm.executePendingTransactions();
    }

    public void mostrarFragmentoNuevaLinea(boolean nuevo, Producto productoModificar){
        FragmentManager fm = getSupportFragmentManager();
        fragmentoNuevaLinea = (FragmentoNuevaLinea)fm.findFragmentByTag(TAG_FRAGMENTO_NUEVO_PRODUCTO);
        FragmentTransaction transaction = fm.beginTransaction();
        if(fragmentoNuevaLinea == null || nuevo){
            fragmentoNuevaLinea = new FragmentoNuevaLinea();
            Bundle b = new Bundle();
            fragmentoNuevaLinea.setArguments(null);
            b.putParcelable("cliente", clienteFactura);
            b.putParcelable("productoModificar", productoModificar);
            fragmentoNuevaLinea.setArguments(b);
        }
        invalidateOptionsMenu();
        transaction.replace(R.id.relativeLayoutFactura, fragmentoNuevaLinea, TAG_FRAGMENTO_NUEVO_PRODUCTO);
        fragmentoActual = ListaFragmentosNuevaFactura.nuevaLinea;
        transaction.addToBackStack(TAG_FRAGMENTO_NUEVO_PRODUCTO);
        transaction.commit();
        fm.executePendingTransactions();
    }

    public void setInicio(boolean inicio){
        this.inicio = inicio;
    }

    /* *************************************************************************
     **************** Interfaz OnClienteSelectedListener ***********************
     *************************************************************************** */

    @Override
    public void devolverCliente(Cliente cliente) {
        this.clienteFactura = cliente;
        mostrarFragmentoNuevaFactura();
        fragmentoPrincipal.setCliente();
    }

    /* *************************************************************************
     **************** Interfaz OnProductoSelectedListener **********************
     *************************************************************************** */

    @Override
    public void devolverProductoLista(Producto producto) {
        FragmentoNuevaFactura.productoModificar = -1;
        mostrarFragmentoNuevaLinea(false, null);
        fragmentoNuevaLinea.setProducto(producto);
    }

    @Override
    public void devolverProducto(Producto producto) {
        mostrarFragmentoNuevaFactura();
        fragmentoPrincipal.setProducto(producto);
    }
}