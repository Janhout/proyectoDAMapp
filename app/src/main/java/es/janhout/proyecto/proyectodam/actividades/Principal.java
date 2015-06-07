package es.janhout.proyecto.proyectodam.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorListaNavigationDrawer;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoContenedorListaFacturas;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoListaClientes;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoListaGastos;
import es.janhout.proyecto.proyectodam.fragmentos.FragmentoListaProductos;
import es.janhout.proyecto.proyectodam.modelos.ItemNavigationDrawer;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class Principal extends AppCompatActivityBusqueda {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private String[] titulos;
    private boolean inicio;
    private SearchView mSearchView;
    private Dialog dialogo;
    private boolean mostrarDialogo;

    private ActionBarDrawerToggle drawerToggle;
    private String tituloActividad;

    private static enum ListaFragmentosPrincipal {
        ninguno,
        clientes,
        gastos,
        facturas,
        productos,
        clientes_favoritos
    }

    private boolean clickDobleSalida;

    public static ListaFragmentosPrincipal fragmentoActual;

    private Toolbar toolbar;

    private final static int ACTIVIDAD_NUEVO_PRODUCTO = 1;
    private final static int ACTIVIDAD_NUEVO_CLIENTE = 2;
    private final static int ACTIVIDAD_NUEVO_GASTO = 3;
    private final static int ACTIVIDAD_NUEVA_FACTURA = 4;

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVIDAD_NUEVO_PRODUCTO:
                    fragmentoActual = ListaFragmentosPrincipal.productos;
                    break;
                case ACTIVIDAD_NUEVO_CLIENTE:
                    fragmentoActual = ListaFragmentosPrincipal.clientes;
                    if (data != null) {
                        startActivity(data);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                    }
                    break;
                case ACTIVIDAD_NUEVA_FACTURA:
                    fragmentoActual = ListaFragmentosPrincipal.facturas;
                    break;
                case ACTIVIDAD_NUEVO_GASTO:
                    fragmentoActual = ListaFragmentosPrincipal.gastos;
                    break;
            }
            busqueda("");
        }
    }

    @Override
    public void onBackPressed() {
        switch (fragmentoActual){
            case ninguno:
            case clientes_favoritos:
                if (clickDobleSalida) {
                    super.onBackPressed();
                    return;
                }
                this.clickDobleSalida = true;
                Metodos.tostada(this, getString(R.string.confirmar_salir));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickDobleSalida = false;
                    }
                }, 1500);
                break;
            case clientes:
            case facturas:
            case gastos:
            case productos:
                seleccionarItem(1);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        inicio = true;
        clickDobleSalida = false;
        if(savedInstanceState != null) {
            inicio = savedInstanceState.getBoolean("fav");
        }
        inicializarToolbar();
        inicializarDrawer();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        cargarFragmentoInicial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_nuevoCliente) {
            nuevoCliente();
            return true;
        } else if(id == R.id.action_nuevoGasto) {
            nuevoGasto();
            return true;
        } else if(id == R.id.action_search) {
            mSearchView.setIconified(false);
            return true;
        } else if(id == R.id.action_nuevaFactura){
            nuevaFactura();
            return true;
        } else if(id == R.id.action_nuevoProducto){
            nuevoProducto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        if(drawerOpen) {
            menu.findItem(R.id.action_nuevoCliente).setVisible(false);
            menu.findItem(R.id.action_nuevoGasto).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_nuevaFactura).setVisible(false);
            menu.findItem(R.id.action_nuevoProducto).setVisible(false);
        } else {
            if (fragmentoActual == ListaFragmentosPrincipal.clientes) {
                menu.findItem(R.id.action_nuevoCliente).setVisible(true);
            } else {
                menu.findItem(R.id.action_nuevoCliente).setVisible(false);
            }
            if (fragmentoActual == ListaFragmentosPrincipal.facturas) {
                menu.findItem(R.id.action_nuevaFactura).setVisible(true);
            } else {
                menu.findItem(R.id.action_nuevaFactura).setVisible(false);
            }
            if (fragmentoActual == ListaFragmentosPrincipal.facturas ||
                    fragmentoActual == ListaFragmentosPrincipal.clientes ||
                    fragmentoActual == ListaFragmentosPrincipal.productos ||
                    fragmentoActual == ListaFragmentosPrincipal.gastos) {
                menu.findItem(R.id.action_search).setVisible(true);
            } else {
                menu.findItem(R.id.action_search).setVisible(false);
            }
            if (fragmentoActual == ListaFragmentosPrincipal.gastos) {
                menu.findItem(R.id.action_nuevoGasto).setVisible(true);
            } else {
                menu.findItem(R.id.action_nuevoGasto).setVisible(false);
            }
            if (fragmentoActual == ListaFragmentosPrincipal.productos) {
                menu.findItem(R.id.action_nuevoProducto).setVisible(true);
            } else {
                menu.findItem(R.id.action_nuevoProducto).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mostrarDialogo = savedInstanceState.getBoolean("mostrarDialogo");
        tituloActividad = savedInstanceState.getString("tituloActividad");
        fragmentoActual = (ListaFragmentosPrincipal)savedInstanceState.getSerializable("actual");
        ActionBar actionBar = getSupportActionBar();
        if(mostrarDialogo){
            mostrarDialogo();
        }
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        if(actionBar != null) {
            if(drawerOpen) {
                actionBar.setTitle(getString(R.string.titulo_navigation_drawer));
                invalidateOptionsMenu();
            } else {
                actionBar.setTitle(tituloActividad);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mostrarDialogo", mostrarDialogo);
        outState.putString("tituloActividad", tituloActividad);
        outState.putBoolean("fav", inicio);
        outState.putSerializable("actual", fragmentoActual);
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
            Fragment fragment = fragmentoClientes(true, "");
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutPrincipal, fragment).commit();
            drawerList.setItemChecked(1, true);
            setTituloActividad(tituloActividad + " - " + titulos[1]);
        }
    }

    public void cerrarDialogo(){
        if(dialogo != null) {
            dialogo.dismiss();
        }
        mostrarDialogo = false;
    }

    private Fragment fragmentoClientes(boolean favorito, String query){
        Fragment fragment = new FragmentoListaClientes();
        if(favorito){
            fragmentoActual = ListaFragmentosPrincipal.clientes_favoritos;
        } else {
            fragmentoActual = ListaFragmentosPrincipal.clientes;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("favorito", favorito);
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        return fragment;
    }

    private Fragment fragmentoProductos(boolean listener, String query){
        Fragment f = new FragmentoListaProductos();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        bundle.putBoolean("listener", false);
        f.setArguments(bundle);
        fragmentoActual = ListaFragmentosPrincipal.productos;
        return f;
    }

    private void inicializarDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        titulos = getResources().getStringArray(R.array.lista_titulos_navigation_drawer);
        String[] iconos = getResources().getStringArray(R.array.lista_iconos_navigation_drawer);

        drawerList = (ListView) findViewById(R.id.lista_drawer);

        ArrayList<ItemNavigationDrawer> items = new ArrayList<>();
        for(int i = 0; i < titulos.length; i++) {
            items.add(new ItemNavigationDrawer(titulos[i], iconos[i]));
        }
        drawerList.setAdapter(new AdaptadorListaNavigationDrawer(this, R.layout.detelle_elemento_drawer, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View view) {
                ActionBar actionBar = getSupportActionBar();
                if(actionBar != null) {
                    actionBar.setTitle(tituloActividad);
                }
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                ActionBar actionBar = getSupportActionBar();
                if(actionBar != null) {
                    actionBar.setTitle(getString(R.string.titulo_navigation_drawer));
                }
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void inicializarToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        fragmentoActual = ListaFragmentosPrincipal.ninguno;
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
        Fragment f = null;
        if (fragmentoActual == ListaFragmentosPrincipal.clientes) {
            f = fragmentoClientes(false, textoBusqueda);
        } else if (fragmentoActual == ListaFragmentosPrincipal.productos) {
            f = fragmentoProductos(false, textoBusqueda);
        } else if (fragmentoActual == ListaFragmentosPrincipal.gastos) {
            f = new FragmentoListaGastos();
            Bundle bundle = new Bundle();
            bundle.putString("query", textoBusqueda);
            f.setArguments(bundle);
        } else if (fragmentoActual == ListaFragmentosPrincipal.facturas) {
            f = new FragmentoContenedorListaFacturas();
            Bundle bundle = new Bundle();
            bundle.putBoolean("todas", true);
            bundle.putString("query", textoBusqueda);
            f.setArguments(bundle);
            fragmentoActual = ListaFragmentosPrincipal.facturas;
        }
        if (f != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutPrincipal, f).commit();
        }
    }

    /* *************************************************************************
     ******************** Métodos items menú ***********************************
     *************************************************************************** */

    private void nuevaFactura() {
        startActivityForResult(new Intent(this, NuevaFactura.class), ACTIVIDAD_NUEVA_FACTURA);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    private void nuevoCliente() {
        startActivityForResult(new Intent(this, NuevoCliente.class), ACTIVIDAD_NUEVO_CLIENTE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    private void nuevoGasto() {
        startActivityForResult(new Intent(this, NuevoGasto.class), ACTIVIDAD_NUEVO_GASTO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    private void nuevoProducto() {
        startActivityForResult(new Intent(this, NuevoProducto.class), ACTIVIDAD_NUEVO_PRODUCTO);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    /* *************************************************************************
     ******************** Gestion Navigation Drawer... *************************
     *************************************************************************** */

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            seleccionarItem(position);
        }
    }

    private void seleccionarItem(int position) {
        Fragment fragment = null;
        inicio = false;
        Intent i;
        switch (position){
            case 0:
                fragment = fragmentoClientes(false, "");
                fragmentoActual = ListaFragmentosPrincipal.clientes;
                break;
            case 1:
                fragment = fragmentoClientes(true, "");
                fragmentoActual = ListaFragmentosPrincipal.clientes_favoritos;
                break;
            case 2:
                fragmentoActual = ListaFragmentosPrincipal.facturas;
                fragment = new FragmentoContenedorListaFacturas();
                Bundle bundle = new Bundle();
                bundle.putBoolean("todas", true);
                bundle.putString("query", "");
                fragment.setArguments(bundle);
                break;
            case 3:
                fragment = new FragmentoListaGastos();
                bundle = new Bundle();
                bundle.putString("query", "");
                fragment.setArguments(bundle);
                fragmentoActual = ListaFragmentosPrincipal.gastos;
                break;
            case 4:
                fragment = fragmentoProductos(false, "");
                break;
            case 5:
                Metodos.borrarPreferenciasCompartidas(this);
                i = new Intent(this, Inicial.class);
                startActivity(i);
                this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                break;
        }

        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutPrincipal, fragment).commit();

            drawerList.setItemChecked(position, true);
            setTituloActividad(titulos[position]);
            drawerLayout.closeDrawer(drawerList);
        }
    }
}