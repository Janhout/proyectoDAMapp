package es.janhout.proyecto.proyectodam.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoDatosCliente;
import es.janhout.proyecto.proyectodam.modelos.Cliente;

public class MostrarCliente extends AppCompatActivityBusqueda {

    private String tituloActividad;
    private Cliente cliente;
    private SearchView searchView;
    private Dialog dialogo;
    private boolean mostrarDialogo;
    private boolean inicio;
    private ItemMenuPulsadoMostrarCliente escuchadorMenu;
    private boolean mostrarTelefono;
    private boolean mostrarEmail;

    public static enum ListaFragmentosCliente {
        ninguno,
        clienteActual,
        facturas
    }

    public static ListaFragmentosCliente fragmentoActual;

    private final static int ACTIVIDAD_NUEVA_FACTURA = 4;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVIDAD_NUEVA_FACTURA:
                    fragmentoActual = ListaFragmentosCliente.facturas;
                    break;
            }
            busqueda("");
        }
    }
    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        switch (fragmentoActual){
            case ninguno:
            case clienteActual:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                break;
            case facturas:
                inicio = true;
                cargarFragmentoInicial();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_cliente);
        inicio = true;
        if(savedInstanceState != null) {
            inicio = savedInstanceState.getBoolean("ini");
        }
        cliente = getIntent().getExtras().getParcelable("cliente");
        inicializarToolbar();
        cargarFragmentoInicial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mostrar_cliente, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
            if(fragmentoActual == ListaFragmentosCliente.clienteActual) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
            } else if (fragmentoActual == ListaFragmentosCliente.facturas){
                inicio = true;
                cargarFragmentoInicial();
            }
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_llamar){
            escuchadorMenu.itemMenuPulsadoMostrarCliente(R.id.action_llamar, null);
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_email) {
            escuchadorMenu.itemMenuPulsadoMostrarCliente(R.id.action_email, null);
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_facturas) {
            escuchadorMenu.itemMenuPulsadoMostrarCliente(R.id.action_facturas, "");
            invalidateOptionsMenu();
            return true;
        } else if(id == R.id.action_nueva_factura){
            Intent i = new Intent(this, NuevaFactura.class);
            Bundle b = new Bundle();
            b.putParcelable("cliente", cliente);
            i.putExtras(b);
            startActivityForResult(i, ACTIVIDAD_NUEVA_FACTURA);
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(fragmentoActual == ListaFragmentosCliente.facturas) {
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_nueva_factura).setVisible(true);
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_nueva_factura).setVisible(false);
        }
        if(fragmentoActual == ListaFragmentosCliente.clienteActual){
            menu.findItem(R.id.action_llamar).setVisible(mostrarTelefono);
            menu.findItem(R.id.action_email).setVisible(mostrarEmail);
            menu.findItem(R.id.action_facturas).setVisible(true);
        } else {
            menu.findItem(R.id.action_llamar).setVisible(false);
            menu.findItem(R.id.action_email).setVisible(false);
            menu.findItem(R.id.action_facturas).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mostrarDialogo = savedInstanceState.getBoolean("mostrarDialogo");
        fragmentoActual = (ListaFragmentosCliente)savedInstanceState.getSerializable("actual");
        if(mostrarDialogo){
            mostrarDialogo();
        }
        setTituloActividad(savedInstanceState.getString("tituloActividad"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mostrarDialogo", mostrarDialogo);
        outState.putBoolean("ini", inicio);
        outState.putSerializable("actual", fragmentoActual);
        outState.putString("tituloActividad", tituloActividad);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cerrarDialogo();
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    private void cargarFragmentoInicial(){
        if(inicio) {
            Fragment fragment = new FragmentoDatosCliente();
            fragmentoActual = ListaFragmentosCliente.clienteActual;
            setTituloActividad(getString(R.string.title_activity_mostrar_cliente));
            Bundle bundle = new Bundle();
            bundle.putParcelable("cliente", cliente);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutCliente, fragment).commit();
        }
    }

    public void cerrarDialogo(){
        if(dialogo != null) {
            dialogo.dismiss();
        }
        mostrarDialogo = false;
    }

    private void inicializarToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fragmentoActual = ListaFragmentosCliente.ninguno;
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

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public void setEscuchadorMenu(ItemMenuPulsadoMostrarCliente escuchadorMenu) {
        this.escuchadorMenu = escuchadorMenu;
    }

    public void setInicio(boolean inicio){
        this.inicio = inicio;
    }

    public void setMostrarTelefono(boolean mostrarTelefono){
        this.mostrarTelefono = mostrarTelefono;
    }

    public void setMostrarEmail(boolean mostrarEmail){
        this.mostrarEmail = mostrarEmail;
    }

    public void setTituloActividad(String tituloActividad){
        this.tituloActividad = tituloActividad;
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(this.tituloActividad);
        }
    }

    /* *************************************************************************
     ************************ Override Busqueda ********************************
     *************************************************************************** */

    @Override
    protected void busqueda(String textoBusqueda) {
        escuchadorMenu.itemMenuPulsadoMostrarCliente(R.id.action_facturas, textoBusqueda);
    }

    /* *************************************************************************
     ******************** Interfaz Menú Pulsado ********************************
     *************************************************************************** */

    public interface ItemMenuPulsadoMostrarCliente {
        public void itemMenuPulsadoMostrarCliente(int itemMenu, String query);
    }
}