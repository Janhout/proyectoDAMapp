package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.MostrarCliente;
import es.janhout.proyecto.proyectodam.actividades.Principal;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorListaClientes;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;
import es.janhout.proyecto.proyectodam.util.ScrollInfinito;

public class FragmentoListaClientes extends Fragment implements AsyncTaskGet.OnProcessCompleteListener,
        AsyncTaskPost.OnProcessCompleteListener {

    private AdaptadorListaClientes ad;
    private ArrayList<Cliente> listaClientes;
    private Principal actividad;

    private ListView lv;
    private TextView textoVacio;

    private int indexContextual;

    private boolean favoritos;
    private String query;

    private int page;
    private static final int LIMITE_CONSULTA = 50;
    private static final int ITEMS_BAJO_LISTA = 5;

    private static final int CODIGO_CONSULTA_CLIENTES = 1;
    private static final int CODIGO_BORRAR_CLIENTE = 2;

    private static final String PARAMETRO_FAVORITOS = "favoritos";
    private static final String PARAMETRO_QUERY = "q";
    private static final String PARAMETRO_LIMIT = "limit";
    private static final String PARAMETRO_PAGE = "page";
    private static final String PARAMETRO_ID = "id_cliente";

    public FragmentoListaClientes() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        page = 0;
        if(savedInstanceState != null) {
            favoritos = savedInstanceState.getBoolean("fav");
        } else {
            favoritos = getArguments().getBoolean("favorito");
        }
        query = getArguments().getString("query");
        inicializarListView();
        cargarLista();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = (Principal)activity;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        indexContextual = info.position;
        if(id == R.id.contextual_lista_borrar){
            borrarCliente();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaClientes = new ArrayList<>();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = actividad.getMenuInflater();
        inflater.inflate(R.menu.contextual_lista,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fav", favoritos);
    }

    /* *************************************************************************
     ******************************* Auxiliares ********************************
     *************************************************************************** */

    private void borrarCliente(){
        actividad.mostrarDialogo();
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_ID, listaClientes.get(indexContextual).getId()+"");
        AsyncTaskPost h = new AsyncTaskPost(actividad, this, Constantes.URL_DELETE_CLIENTE, CODIGO_BORRAR_CLIENTE);
        h.execute(parametros);
    }

    private void cargarLista(){
        String url = Constantes.URL_GET_CLIENTES;
        Hashtable<String, String> parametros = new Hashtable<>();
        if (favoritos){
            parametros.put(PARAMETRO_FAVORITOS, "true");
        } else {
            parametros.put(PARAMETRO_FAVORITOS, "false");
        }
        parametros.put(PARAMETRO_QUERY, query);
        parametros.put(PARAMETRO_PAGE, page+"");
        parametros.put(PARAMETRO_LIMIT, LIMITE_CONSULTA+"");
        AsyncTaskGet asyncTask = new AsyncTaskGet(actividad, this, url, false, CODIGO_CONSULTA_CLIENTES);
        asyncTask.execute(parametros);
    }

    private void inicializarListView() {
        if(listaClientes != null) {
            if (getView() != null) {
                lv = (ListView) getView().findViewById(R.id.lvLista);
                textoVacio = (TextView) getView().findViewById(R.id.empty);
                ad = new AdaptadorListaClientes(getActivity(), R.layout.detalle_lista_cliente, listaClientes);
                lv.setAdapter(ad);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(actividad, MostrarCliente.class);
                        Bundle b = new Bundle();
                        b.putParcelable("cliente", listaClientes.get(position));
                        i.putExtras(b);
                        actividad.startActivity(i);
                        actividad.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
                    }
                });
                lv.setOnScrollListener(new ScrollInfinito(ITEMS_BAJO_LISTA) {
                    @Override
                    public void cargaMas(int page, int totalItemsCount) {
                        FragmentoListaClientes.this.page = page;
                        cargarLista();
                    }
                });
                registerForContextMenu(lv);
            }
        }
    }

    /* *************************************************************************
    **************** Interfaz OnProcessCompleteListener ***********************
    *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo) {
        textoVacio.setText(actividad.getString(R.string.lista_clientes_vacia));
        lv.setEmptyView(textoVacio);
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo) {
                    case CODIGO_CONSULTA_CLIENTES:
                        JSONTokener token = new JSONTokener(respuesta);
                        JSONArray array;
                        try {
                            array = new JSONArray(token);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                listaClientes.add(new Cliente(obj));
                            }
                            if (ad != null) {
                                ad.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            listaClientes = null;
                        }
                        break;
                }
            }
        } else {
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }

    @Override
    public void resultadoPost(String respuesta, int codigo_peticion) {
        actividad.cerrarDialogo();
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo_peticion) {
                    case CODIGO_BORRAR_CLIENTE:
                        if(respuesta.equals("r5")){
                            Metodos.tostada(actividad, actividad.getString(R.string.cliente_borrado));
                            listaClientes.remove(indexContextual);
                            ad.notifyDataSetChanged();
                        } else {
                            Metodos.tostada(actividad, actividad.getString(R.string.error_borrar_cliente));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }
}