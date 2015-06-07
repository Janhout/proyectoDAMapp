package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.Principal;
import es.janhout.proyecto.proyectodam.adaptadores.AdaptadorListaGastos;
import es.janhout.proyecto.proyectodam.modelos.Gasto;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;
import es.janhout.proyecto.proyectodam.util.ScrollInfinito;

public class FragmentoListaGastos extends Fragment implements AsyncTaskGet.OnProcessCompleteListener {

    private AdaptadorListaGastos ad;
    private ArrayList<Gasto> listaGastos;
    private Activity actividad;
    private int page;
    private String query;

    private TextView textoVacio;
    private ListView lv;

    private static final int LIMITE_CONSULTA = 50;
    private static final int ITEMS_BAJO_LISTA = 5;

    private static final int CODIGO_CONSULTA_GASTOS = 1;
    private static final int CODIGO_PEDIR_FOTO = 2;

    private static final String PARAMETRO_QUERY = "q";
    private static final String PARAMETRO_LIMIT = "limit";
    private static final String PARAMETRO_PAGE = "page";
    private static final String PARAMETRO_ID = "id";

    public FragmentoListaGastos() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        page = 0;
        query = getArguments().getString("query");
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
        listaGastos = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    private void cargarGastos(String respuesta){
        JSONTokener token = new JSONTokener(respuesta);
        JSONArray array;
        try {
            array = new JSONArray(token);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Gasto g = new Gasto(obj);
                listaGastos.add(g);
                if (ad != null) {
                    ad.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            listaGastos = null;
        }
    }

    private void cargarLista() {
        Hashtable<String, String> parametros = new Hashtable<>();
        String url = Constantes.URL_GET_GASTOS;
        parametros.put(PARAMETRO_QUERY, query);
        parametros.put(PARAMETRO_PAGE, page+"");
        parametros.put(PARAMETRO_LIMIT, LIMITE_CONSULTA+"");
        AsyncTaskGet asyncTask = new AsyncTaskGet(actividad, this, url, false, CODIGO_CONSULTA_GASTOS);
        asyncTask.execute(parametros);
    }

    private void inicializarListView(){
        if (listaGastos!= null) {
            if (getView() != null) {
                lv = (ListView) getView().findViewById(R.id.lvLista);
                textoVacio = (TextView) getView().findViewById(R.id.empty);
                ad = new AdaptadorListaGastos(getActivity(), R.layout.detalle_lista_gastos, listaGastos);
                lv.setAdapter(ad);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        verGasto(position);
                    }
                });
                lv.setOnScrollListener(new ScrollInfinito(ITEMS_BAJO_LISTA) {
                    @Override
                    public void cargaMas(int page, int totalItemsCount) {
                        FragmentoListaGastos.this.page = page;
                        cargarLista();
                    }
                });
            }
        }
    }

    private void mostrarFotoGasto(String respuesta){
        Dialog builder = new Dialog(actividad);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        ImageView imageView = new ImageView(actividad);
        imageView.setImageBitmap(BitmapFactory.decodeFile(respuesta));
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    private void verGasto(int position){
        if(!listaGastos.get(position).getRutaFoto().trim().equals("")) {
            ((Principal) actividad).mostrarDialogo();
            String url = Constantes.URL_GET_FOTO_GASTO;
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put(PARAMETRO_ID, listaGastos.get(position).getId() + "");
            AsyncTaskGet a = new AsyncTaskGet(actividad, this, url, true, CODIGO_PEDIR_FOTO);
            a.execute(parametros);
        } else {
            Metodos.tostada(actividad, actividad.getString(R.string.no_hay_foto));
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo){
        textoVacio.setText(actividad.getString(R.string.lista_gastos_vacia));
        lv.setEmptyView(textoVacio);
        ((Principal) actividad).cerrarDialogo();
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo) {
                    case CODIGO_CONSULTA_GASTOS:
                        cargarGastos(respuesta);
                        break;
                    case CODIGO_PEDIR_FOTO:
                        if(!respuesta.equals("r18")) {
                            mostrarFotoGasto(respuesta);
                        } else {
                            Metodos.tostada(actividad, actividad.getString(R.string.no_hay_foto));
                        }
                        break;
                }
            }
        } else {
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }
}