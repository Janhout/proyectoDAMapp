package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
import es.janhout.proyecto.proyectodam.actividades.LectorPDF;
import es.janhout.proyecto.proyectodam.actividades.MostrarCliente;
import es.janhout.proyecto.proyectodam.actividades.Principal;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorListaFacturas;
import es.janhout.proyecto.proyectodam.modelos.Factura;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;
import es.janhout.proyecto.proyectodam.util.ScrollInfinito;

public class FragmentoListaFacturas extends Fragment implements AsyncTaskGet.OnProcessCompleteListener{

    private AdaptadorListaFacturas ad;
    private ArrayList<Factura> listaFacturas;
    private Activity actividad;
    private int page;
    private int idCliente;
    private boolean todas;
    private String query;

    private int estadoFactura;

    private TextView textoVacio;
    private ListView lv;

    private static final int LIMITE_CONSULTA = 50;
    private static final int ITEMS_BAJO_LISTA = 5;

    private static final int CODIGO_CONSULTA_FACTURAS = 1;
    private static final int CODIGO_PEDIR_PDF = 2;

    private static final String PARAMETRO_QUERY = "q";
    private static final String PARAMETRO_LIMIT = "limit";
    private static final String PARAMETRO_PAGE = "page";
    private static final String PARAMETRO_ESTADO = "estado";
    private static final String PARAMETRO_ID_CLIENTE = "id_cliente";
    private static final String PARAMETRO_ID = "id";

    public static FragmentoListaFacturas newInstance(boolean todasFacturas, String query, int idCliente, int estadoFactura) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("todo", todasFacturas);
        bundle.putString("query", query);
        bundle.putInt("idCliente", idCliente);
        bundle.putInt("estadoFactura", estadoFactura);

        FragmentoListaFacturas fragmento = new FragmentoListaFacturas();
        fragmento.setArguments(bundle);

        return fragmento;
    }

    public FragmentoListaFacturas() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        page = 0;
        inicializarListView();
        cargarLista();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaFacturas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            todas = args.getBoolean("todo");
            query = args.getString("query");
            idCliente = args.getInt("idCliente");
            estadoFactura = args.getInt("estadoFactura");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("all", todas);
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    private void cargarFacturas(String respuesta){
        JSONTokener token = new JSONTokener(respuesta);
        JSONArray array;
        try {
            array = new JSONArray(token);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Factura f = new Factura(obj);
                if(!listaFacturas.contains(f)) {
                    listaFacturas.add(f);
                }
                if (ad != null) {
                    ad.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            listaFacturas = null;
        }
    }

    private void cargarLista() {
        AsyncTaskGet asyncTask;
        String url;
        Hashtable<String, String> parametros = new Hashtable<>();
        url = Constantes.URL_GET_FACTURAS;
        switch (estadoFactura){
            case 1:
                parametros.put(PARAMETRO_ESTADO, "Pagada");
                break;
            case 2:
                parametros.put(PARAMETRO_ESTADO, "Borrador");
                break;
            case 0:
                parametros.put(PARAMETRO_ESTADO, "Impagada");
                break;
        }
        if (!todas){
            parametros.put(PARAMETRO_ID_CLIENTE, idCliente+"");
        }
        parametros.put(PARAMETRO_QUERY, query);
        parametros.put(PARAMETRO_PAGE, page+"");
        parametros.put(PARAMETRO_LIMIT, LIMITE_CONSULTA+"");
        asyncTask = new AsyncTaskGet(actividad, this, url, false, CODIGO_CONSULTA_FACTURAS);
        asyncTask.execute(parametros);
    }

    private void inicializarListView(){
        if (listaFacturas != null) {
            if (getView() != null) {
                lv = (ListView) getView().findViewById(R.id.lvLista);
                textoVacio = (TextView) getView().findViewById(R.id.empty);
                ad = new AdaptadorListaFacturas(getActivity(), R.layout.detalle_lista_factura, listaFacturas);
                lv.setAdapter(ad);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        verFactura(position);
                    }
                });
                lv.setOnScrollListener(new ScrollInfinito(ITEMS_BAJO_LISTA) {
                    @Override
                    public void cargaMas(int page, int totalItemsCount) {
                        FragmentoListaFacturas.this.page = page;
                        cargarLista();
                    }
                });
            }
        }
    }

    private void intentFactura(String respuesta){
        Intent intentCompartir = new Intent(actividad, LectorPDF.class);
        intentCompartir.putExtra("pdf", respuesta);
        actividad.startActivity(intentCompartir);
    }

    private void verFactura(int position){
        String url = Constantes.URL_GET_PDF_FACTURA;
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_ID, listaFacturas.get(position).getIdFactura()+"");
        AsyncTaskGet a = new AsyncTaskGet(actividad, this, url, true, CODIGO_PEDIR_PDF);
        if(actividad.getClass().getName().contains(MostrarCliente.class.getSimpleName())) {
            ((MostrarCliente) actividad).mostrarDialogo();
        } else if(actividad.getClass().getName().contains(Principal.class.getSimpleName())){
            ((Principal) actividad).mostrarDialogo();
        }
        a.execute(parametros);
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo){
        textoVacio.setText(getString(R.string.lista_facturas_vacia));
        lv.setEmptyView(textoVacio);
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo) {
                    case CODIGO_CONSULTA_FACTURAS:
                        cargarFacturas(respuesta);
                        break;
                    case CODIGO_PEDIR_PDF:
                        if (actividad.getClass().getName().contains(MostrarCliente.class.getSimpleName())) {
                            ((MostrarCliente) actividad).cerrarDialogo();
                        } else if (actividad.getClass().getName().contains(Principal.class.getSimpleName())) {
                            ((Principal) actividad).cerrarDialogo();
                        }
                        intentFactura(respuesta);
                        break;
                }
            }
        } else {
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }
}

