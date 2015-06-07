package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorSeleccionarCliente;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;
import es.janhout.proyecto.proyectodam.util.ScrollInfinito;

public class FragmentoSeleccionarCliente extends Fragment implements AsyncTaskGet.OnProcessCompleteListener{

    private ArrayList<Cliente> listaClientes;
    private NuevaFactura actividad;
    private AdaptadorSeleccionarCliente ad;
    private String query;
    private OnClienteSelectedListener listener;

    private ListView lv;
    private TextView textoVacio;

    private int page;
    private static final int LIMITE_CONSULTA = 50;
    private static final int ITEMS_BAJO_LISTA = 5;

    private static final int CODIGO_CONSULTA_CLIENTES = 1;

    private static final String PARAMETRO_QUERY = "q";
    private static final String PARAMETRO_LIMIT = "limit";
    private static final String PARAMETRO_PAGE = "page";

    public FragmentoSeleccionarCliente() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        query = getArguments().getString("query");
        listener = (NuevaFactura)getActivity();
        inicializarListView();
        cargarLista();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = (NuevaFactura)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaClientes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    /* *************************************************************************
     ******************************* Auxiliares ********************************
     *************************************************************************** */

    private void cargarLista(){
        actividad.mostrarDialogo();
        String url = Constantes.URL_GET_CLIENTES;
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_QUERY, query);
        parametros.put(PARAMETRO_PAGE, page+"");
        parametros.put(PARAMETRO_LIMIT, LIMITE_CONSULTA+"");
        AsyncTaskGet asyncTask = new AsyncTaskGet(actividad, this, url, false, CODIGO_CONSULTA_CLIENTES);
        asyncTask.execute(parametros);
    }

    private void inicializarListView(){
        if(listaClientes != null) {
            if (getView() != null) {
                lv = (ListView) getView().findViewById(R.id.lvLista);
                textoVacio = (TextView) getView().findViewById(R.id.empty);
                ad = new AdaptadorSeleccionarCliente(getActivity(), R.layout.detalle_seleccionar_cliente, listaClientes);
                lv.setAdapter(ad);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listener.devolverCliente(listaClientes.get(position));
                    }
                });
                lv.setOnScrollListener(new ScrollInfinito(ITEMS_BAJO_LISTA) {
                    @Override
                    public void cargaMas(int page, int totalItemsCount) {
                        FragmentoSeleccionarCliente.this.page = page;
                        cargarLista();
                    }
                });
            }
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo) {
        textoVacio.setText(actividad.getString(R.string.lista_clientes_vacia));
        lv.setEmptyView(textoVacio);
        actividad.cerrarDialogo();
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

    /* *************************************************************************
     ******************** Interfaz OnClienteSelectedListener *******************
     *************************************************************************** */

    public interface OnClienteSelectedListener{
        public void devolverCliente(Cliente cliente);
    }
}