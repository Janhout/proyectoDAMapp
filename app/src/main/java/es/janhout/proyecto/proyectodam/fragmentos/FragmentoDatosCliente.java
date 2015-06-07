package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.MostrarCliente;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.util.AsyncTaskGet;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class FragmentoDatosCliente extends Fragment implements AsyncTaskGet.OnProcessCompleteListener,
        MostrarCliente.ItemMenuPulsadoMostrarCliente {

    private MostrarCliente actividad;
    private Cliente clienteMostrar;

    private TextView nif;
    private TextView nombreComercial;
    private TextView direccion;
    private TextView telefonos;
    private TextView email;
    private TextView numeroCuenta;

    private Button favorito;

    private static final int CODIGO_SET_FAVORITO = 2;

    private static final String PARAMETRO_ID = "id";

    public FragmentoDatosCliente() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = (MostrarCliente)activity;
        actividad.setEscuchadorMenu(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_cliente, container, false);
        clienteMostrar = getArguments().getParcelable("cliente");
        actividad.setInicio(false);
        inicializarViews(v);
        mostrarDatosCliente();
        return v;
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    private void inicializarEventosBotones() {
        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarFavorito();
            }
        });
    }

    private void inicializarViews(View v) {
        nombreComercial = (TextView) v.findViewById(R.id.mostrarCliente_nombrecomercial);
        nif = (TextView) v.findViewById(R.id.mostrarCliente_nif);
        telefonos = (TextView) v.findViewById(R.id.mostrarCliente_telefonos);
        direccion = (TextView) v.findViewById(R.id.mostrarCliente_direccion);
        email = (TextView) v.findViewById(R.id.mostrarCliente_email);
        numeroCuenta = (TextView) v.findViewById(R.id.mostrarCliente_numeroCuenta);

        favorito = (Button) v.findViewById(R.id.bt_favorito);

        inicializarEventosBotones();
    }

    private void mostrarDatosCliente() {
        direccion.setText(clienteMostrar.getDireccion());
        nombreComercial.setText(clienteMostrar.getNombre_comercial());
        nif.setText(clienteMostrar.getNif());
        String telfs = "";
        if (!clienteMostrar.getTelefono01().equals("") && !clienteMostrar.getTelefono02().equals("")) {
            telfs = clienteMostrar.getTelefono01() + " / " + clienteMostrar.getTelefono02();
        } else if (!clienteMostrar.getTelefono01().equals("")) {
            telfs = clienteMostrar.getTelefono01();
        } else if (!clienteMostrar.getTelefono02().equals("")) {
            telfs = clienteMostrar.getTelefono02();
        }
        telefonos.setText(telfs);
        email.setText(clienteMostrar.getEmail());
        numeroCuenta.setText(clienteMostrar.getNumero_cuenta());

        boolean telefono = true;
        boolean email = true;
        String telf1 = clienteMostrar.getTelefono01().replaceAll(" ", "");
        String telf2 = clienteMostrar.getTelefono02().replaceAll(" ", "");
        if(!TextUtils.isDigitsOnly(telf1)){
            telf1 = "";
        }
        if(!TextUtils.isDigitsOnly(telf2)){
            telf2 = "";
        }
        if(telf1.equals("") && telf2.equals("")){
            telefono = false;
        }
        if(clienteMostrar.getEmail().trim().equals("")){
            email = false;
        }

        actividad.setMostrarEmail(email);
        actividad.setMostrarTelefono(telefono);
        actividad.invalidateOptionsMenu();

        if (clienteMostrar.isFavorito()) {
            Metodos.botonAwesomeComponente(actividad, favorito, getString(R.string.icono_clientes_favoritos));
        } else {
            Metodos.botonAwesomeComponente(actividad, favorito, getString(R.string.icono_clientes_no_favoritos));
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoGet(String respuesta, int codigo) {
        actividad.cerrarDialogo();
        if (respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo) {
                    case CODIGO_SET_FAVORITO:
                        clienteMostrar.setFavorito(!clienteMostrar.isFavorito());
                        if (clienteMostrar.isFavorito()) {
                            Metodos.botonAwesomeComponente(actividad, favorito, getString(R.string.icono_clientes_favoritos));
                        } else {
                            Metodos.botonAwesomeComponente(actividad, favorito, getString(R.string.icono_clientes_no_favoritos));
                        }
                        break;
                }
            }
        } else {
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }

    /* *************************************************************************
     ******************** Interfaz ItemMenuPulsado *****************************
     *************************************************************************** */

    @Override
    public void itemMenuPulsadoMostrarCliente(int itemMenu, String query) {
        switch (itemMenu) {
            case R.id.action_llamar:
                llamarTelefono();
                break;
            case R.id.action_email:
                enviarEmail();
                break;
            case R.id.action_facturas:
                verFacturas(query);
                break;
        }
    }

    /* *************************************************************************
     ******************************* Métodos Botones ***************************
     *************************************************************************** */

    private void cambiarFavorito() {
        actividad.mostrarDialogo();
        String url = Constantes.SET_FAVORITO;
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_ID, clienteMostrar.getId()+"");
        AsyncTaskGet h = new AsyncTaskGet(actividad, this, url, false, CODIGO_SET_FAVORITO);
        h.execute(parametros);
    }

    /* *************************************************************************
     ******************************* Auxiliares ********************************
     *************************************************************************** */

    private void seleccionarTelefono(String telefono1, String telefono2) {
        final CharSequence telfs[] = new CharSequence[]{telefono1, telefono2};
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(getString(R.string.selecciona_telefono));
        builder.setItems(telfs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uri = "tel:" + telfs[which].toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
        builder.show();
    }

    /* *************************************************************************
     ************************** Métodos Botones Menú ***************************
     *************************************************************************** */

    public void enviarEmail() {
        if (!clienteMostrar.getEmail().trim().equals("")) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{clienteMostrar.getEmail().trim()});
            try {
                startActivity(Intent.createChooser(i, getString(R.string.enviar_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Metodos.tostada(actividad, getString(R.string.no_app_disponible));
            }
        }
    }

    public void llamarTelefono() {
        String telefono01, telefono02;

        telefono01 = clienteMostrar.getTelefono01().trim();
        telefono02 = clienteMostrar.getTelefono02().trim();

        String uri = "";
        if (!telefono01.equals("") && !telefono02.equals("")) {
            seleccionarTelefono(telefono01, telefono02);
        } else if (!telefono01.equals("")) {
            uri = "tel:" + telefono01;
        } else if (!telefono02.equals("")) {
            uri = "tel:" + telefono02;
        }

        if (!uri.equals("")) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
    }

    private void verFacturas(String query) {
        Bundle bundle = new Bundle();
        bundle.putInt("idCliente", clienteMostrar.getId());
        bundle.putBoolean("todas", false);
        bundle.putString("query", query);
        MostrarCliente.fragmentoActual = MostrarCliente.ListaFragmentosCliente.facturas;
        Fragment fragmento = new FragmentoContenedorListaFacturas();
        fragmento.setArguments(bundle);
        FragmentTransaction transaction = actividad.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayoutCliente, fragmento);
        transaction.addToBackStack(null);
        actividad.setTituloActividad("Facturas - " + clienteMostrar.getNombre_comercial());
        transaction.commit();
    }
}