package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import es.janhout.proyecto.proyectodam.actividades.NuevaFactura;
import es.janhout.proyecto.proyectodam.actividades.Principal;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorListaProductos;
import es.janhout.proyecto.proyectodam.modelos.Producto;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;
import es.janhout.proyecto.proyectodam.util.ScrollInfinito;

public class FragmentoListaProductos extends Fragment implements AsyncTaskGet.OnProcessCompleteListener,
        AsyncTaskPost.OnProcessCompleteListener{

    private ArrayList<Producto> listaProductos;
    private Activity actividad;
    private AdaptadorListaProductos ad;
    private String query;
    private OnProductoListaSelectedListener listener;

    private int indexContextual;

    private ListView lv;
    private TextView textoVacio;

    private int page;
    private static final int LIMITE_CONSULTA = 50;
    private static final int ITEMS_BAJO_LISTA = 5;

    private static final int CODIGO_CONSULTA_PRODUCTOS = 1;
    private static final int CODIGO_BORRAR_PRODUCTO = 2;

    private static final String PARAMETRO_QUERY = "q";
    private static final String PARAMETRO_LIMIT = "limit";
    private static final String PARAMETRO_PAGE = "page";
    private static final String PARAMETRO_ID_PRODUCTO = "id_producto";

    public FragmentoListaProductos() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        query = getArguments().getString("query");

        if (getArguments().getBoolean("listener")) {
            listener = (NuevaFactura) getActivity();
        }
        else {
            listener = null;
        }
        inicializarListView();
        cargarLista();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = activity;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        indexContextual = info.position;
        if(id == R.id.contextual_lista_borrar){
            borrarProducto();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaProductos = new ArrayList<>();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = actividad.getMenuInflater();
        inflater.inflate(R.menu.contextual_lista,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    /* *************************************************************************
     ******************************* Auxiliares ********************************
     *************************************************************************** */

    private void borrarProducto(){
        if(actividad.getClass().getName().contains(NuevaFactura.class.getSimpleName())) {
            ((NuevaFactura) actividad).mostrarDialogo();
        } else if(actividad.getClass().getName().contains(Principal.class.getSimpleName())){
            ((Principal) actividad).mostrarDialogo();
        }
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_ID_PRODUCTO, listaProductos.get(indexContextual).getId_a()+"");
        AsyncTaskPost h = new AsyncTaskPost(actividad, this, Constantes.URL_DELETE_PRODUCTO, CODIGO_BORRAR_PRODUCTO);
        h.execute(parametros);
    }

    private void cargarLista(){
        if(actividad.getClass().getName().contains(NuevaFactura.class.getSimpleName())) {
            ((NuevaFactura) actividad).mostrarDialogo();
        } else if(actividad.getClass().getName().contains(Principal.class.getSimpleName())){
            ((Principal) actividad).mostrarDialogo();
        }
        String url = Constantes.URL_GET_PRODUCTOS;
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_QUERY, query);
        parametros.put(PARAMETRO_PAGE, page+"");
        parametros.put(PARAMETRO_LIMIT, LIMITE_CONSULTA+"");
        AsyncTaskGet asyncTask = new AsyncTaskGet(actividad, this, url, false, CODIGO_CONSULTA_PRODUCTOS);
        asyncTask.execute(parametros);
    }

    private void inicializarListView(){
        if(listaProductos != null) {
            if (getView() != null) {
                lv = (ListView) getView().findViewById(R.id.lvLista);
                textoVacio = (TextView) getView().findViewById(R.id.empty);
                ad = new AdaptadorListaProductos(getActivity(), R.layout.detalle_lista_producto, listaProductos);
                lv.setAdapter(ad);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listener != null) {
                            listener.devolverProductoLista(listaProductos.get(position));
                        }
                    }
                });
                lv.setOnScrollListener(new ScrollInfinito(ITEMS_BAJO_LISTA) {
                    @Override
                    public void cargaMas(int page, int totalItemsCount) {
                        FragmentoListaProductos.this.page = page;
                        cargarLista();
                    }
                });
                if(listener == null){
                    registerForContextMenu(lv);
                }
            }
        }
    }

    /* *************************************************************************
     ***************** Interfaz OnProcessCompleteListener **********************
     *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo) {
        textoVacio.setText(getString(R.string.lista_productos_vacia));
        lv.setEmptyView(textoVacio);
        if (actividad.getClass().getName().contains(NuevaFactura.class.getSimpleName())) {
            ((NuevaFactura) actividad).cerrarDialogo();
        } else if (actividad.getClass().getName().contains(Principal.class.getSimpleName())) {
            ((Principal) actividad).cerrarDialogo();
        }
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo) {
                    case CODIGO_CONSULTA_PRODUCTOS:
                        JSONTokener token = new JSONTokener(respuesta);
                        JSONArray array;
                        try {
                            array = new JSONArray(token);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                listaProductos.add(new Producto(obj));
                            }
                            if (ad != null) {
                                ad.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            listaProductos = null;
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
        if (actividad.getClass().getName().contains(NuevaFactura.class.getSimpleName())) {
            ((NuevaFactura) actividad).cerrarDialogo();
        } else if (actividad.getClass().getName().contains(Principal.class.getSimpleName())) {
            ((Principal) actividad).cerrarDialogo();
        }
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                Log.v("mio", respuesta);
                switch (codigo_peticion) {
                    case CODIGO_BORRAR_PRODUCTO:
                        if(respuesta.equals("r5")){
                            Metodos.tostada(actividad, actividad.getString(R.string.producto_borrado));
                            listaProductos.remove(indexContextual);
                            ad.notifyDataSetChanged();
                        } else {
                            Metodos.tostada(actividad, actividad.getString(R.string.error_borrar_producto));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProductoSeleccionado **********************
     *************************************************************************** */

    public interface OnProductoListaSelectedListener{
        public void devolverProductoLista(Producto producto);
    }
}