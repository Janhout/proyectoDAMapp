package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.NuevaFactura;
import es.janhout.proyecto.proyectodam.modelos.Cliente;
import es.janhout.proyecto.proyectodam.modelos.Producto;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class FragmentoNuevaLinea extends Fragment {

    private NuevaFactura actividad;
    private Cliente cliente;
    private Producto producto;

    private boolean modificar;

    private EditText etNombreProducto;
    private EditText etCantidadProducto;
    private EditText etPrecioProducto;
    private EditText etDescripcionProducto;
    private EditText etSeleccionarProducto;

    private Spinner spIva;

    private String[] lista_iva;

    private OnProductoSelectedListener listener;

    public FragmentoNuevaLinea() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (NuevaFactura)getActivity();
        cliente = this.getArguments().getParcelable("cliente");
        producto = this.getArguments().getParcelable("productoModificar");
        modificar = (producto!=null);
        cargarView();
        if(producto != null){
            mostrarDatosProducto();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actividad = (NuevaFactura)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nueva_linea, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_guardar_factura){
            if(NuevaFactura.fragmentoActual == NuevaFactura.ListaFragmentosNuevaFactura.nuevaLinea){
                if(!etNombreProducto.getText().toString().trim().equals("")) {
                    if (producto == null ||
                            !producto.getNombre_producto().equals(etNombreProducto.getText().toString())) {
                        producto = new Producto();
                        producto.setReferencia_producto(".");
                    }
                    String precio = etPrecioProducto.getText().toString().equals("") ? "0" : etPrecioProducto.getText().toString();
                    producto.setPrecio_venta(Metodos.stringToDouble(precio) + "");
                    String cantidad = etCantidadProducto.getText().toString();
                    producto.setCantidad(cantidad);
                    producto.setNombre_producto(etNombreProducto.getText().toString());
                    producto.setDescripcion(etDescripcionProducto.getText().toString());
                    producto.setTipo_iva(spIva.getSelectedItem().toString());
                    listener.devolverProducto(producto);
                } else {
                    Metodos.tostada(actividad, actividad.getString(R.string.nueva_factura_no_producto));
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    private void cargarView(){
        if(getView()!= null) {
            cargarViewProducto();
            cargarViewInfoLinea();
            cargarViewDescripcion();
        }
    }

    private void cargarViewDescripcion(){
        if(getView() != null) {
            etDescripcionProducto = (EditText) getView().findViewById(R.id.nueva_factura_et_descripcion_producto);
        }
        etDescripcionProducto.setEnabled(false);
    }

    private void cargarViewInfoLinea(){
        if(getView() != null) {
            etCantidadProducto = (EditText) getView().findViewById(R.id.nueva_factura_et_cantidad_producto);
            etPrecioProducto = (EditText) getView().findViewById(R.id.nueva_factura_et_precio_producto);
            spIva = (Spinner) getView().findViewById(R.id.nueva_factura_sp_iva_producto);
            etPrecioProducto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && ((EditText) v).getText().toString().equals("")) {
                        etPrecioProducto.setText(Metodos.doubleToString(0.00));
                    } else if (hasFocus && ((EditText) v).getText().toString().equals(Metodos.doubleToString(0.00))) {
                        etPrecioProducto.setText("");
                    }
                }
            });
            lista_iva = getResources().getStringArray(R.array.lista_iva);
            ArrayAdapter<String> adapter = new ArrayAdapter(actividad, android.R.layout.simple_spinner_item, lista_iva);            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spIva.setAdapter(adapter);
            etPrecioProducto.setText(Metodos.doubleToString(0.00));
            etCantidadProducto.setText("1");
            spIva.setSelection(0);
            spIva.setEnabled(false);
            etPrecioProducto.setEnabled(false);
        }
    }

    private void cargarViewProducto(){
        if(getView() != null) {
            etSeleccionarProducto = (EditText) getView().findViewById(R.id.nueva_factura_et_producto);
            etNombreProducto = (EditText) getView().findViewById(R.id.nueva_factura_et_nombre_producto);
            etSeleccionarProducto.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Fragment fragment = new FragmentoListaProductos();
                        NuevaFactura.fragmentoActual = NuevaFactura.ListaFragmentosNuevaFactura.seleccionarProducto;
                        Bundle bundle = new Bundle();
                        bundle.putString("query", "");
                        bundle.putBoolean("listener", true);
                        fragment.setArguments(bundle);
                        FragmentoNuevaLinea.this.getActivity().invalidateOptionsMenu();
                        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.relativeLayoutFactura, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        getFragmentManager().executePendingTransactions();
                    }
                    return true;
                }
            });
            etNombreProducto.setEnabled(false);
            etSeleccionarProducto.setEnabled(!modificar);
        }
    }

    private void mostrarDatosProducto(){
        etSeleccionarProducto.setText(producto.getReferencia_producto());
        etNombreProducto.setText(producto.getNombre_producto());
        etCantidadProducto.setText(producto.getCantidad());
        etPrecioProducto.setText(producto.getPrecio_venta());
        etDescripcionProducto.setText(producto.getDescripcion());
        boolean buscar = true;
        int i;
        for (i = 0; i < lista_iva.length && buscar; i++) {
            if(lista_iva[i].contains(producto.getTipo_iva())){
                buscar = false;
            }
        }
        spIva.setSelection(i-1);
    }

    public void setProducto(Producto producto){
        this.producto = producto.clonar();
        mostrarDatosProducto();
    }

    /* *************************************************************************
     ******************** Interfaz producto seleccionado ***********************
     *************************************************************************** */

    public interface OnProductoSelectedListener{
        public void devolverProducto(Producto producto);
    }
}