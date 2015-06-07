package es.janhout.proyecto.proyectodam.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import es.janhout.proyecto.proyectodam.R;
import es.janhout.proyecto.proyectodam.actividades.NuevaFactura;
import es.janhout.proyecto.proyectodam.modelos.Producto;
import es.janhout.proyecto.proyectodam.util.AsyncTaskPost;
import es.janhout.proyecto.proyectodam.util.Constantes;
import es.janhout.proyecto.proyectodam.util.Metodos;

public class FragmentoNuevaFactura extends Fragment implements OnDateSetListener,
        AsyncTaskPost.OnProcessCompleteListener {

    private NuevaFactura actividad;
    private EditText etCliente;
    private EditText etFechaFactura;
    private EditText etFechaVenciminetoFactura;
    private RadioButton formatoNeto;
    private LinearLayout llLineas;
    private LinearLayout llTotalesIva;
    private ArrayList<Producto> listaProductos;
    private Spinner spCondiciones_pago;
    private EditText etNotas;
    private TextView tvSubtotal;
    private TextView tvTotal;

    public static final String FECHA_FACTURA_TAG = "fecha_factura";
    public static final String FECHA_VENCIMIENTO_TAG = "fecha_vencimiento";

    public static final int CODIGO_PETICION_FACTURA = 1;

    private static final String PARAMETRO_FECHA = "fecha";
    private static final String PARAMETRO_CLIENTE = "id_cliente";
    private static final String PARAMETRO_NOTAS = "notas";
    private static final String PARAMETRO_VENCIMIENTO = "fecha_vencimiento";
    private static final String PARAMETRO_PRODUCTOS = "productos";

    public static int productoModificar;

    public FragmentoNuevaFactura() {
    }

    /* *************************************************************************
     **************************** Métodos on... ********************************
     *************************************************************************** */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cargarView();
        if(savedInstanceState != null) {
            listaProductos = (ArrayList) savedInstanceState.getParcelableArrayList("listaProductos");
        }
        if(listaProductos == null) {
            listaProductos = new ArrayList<>();
        }
        if(actividad.getClienteFactura() != null){
            setCliente();
        }
        cargarListaProductos();
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
        actividad.setInicio(false);
        return inflater.inflate(R.layout.fragment_nueva_factura, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_guardar_factura){
            if(NuevaFactura.fragmentoActual == NuevaFactura.ListaFragmentosNuevaFactura.nuevaFactura){
                if(listaProductos.size()<1){
                    Metodos.tostada(actividad, actividad.getString(R.string.nueva_factura_no_producto));
                } else if(actividad.getClienteFactura() == null) {
                    Metodos.tostada(actividad, actividad.getString(R.string.nueva_factura_no_cliente));
                } else {
                    guardarFactura();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listaProductos", listaProductos);
    }

    /* *************************************************************************
     **************************** Auxialiares **********************************
     *************************************************************************** */

    private void accionLinea(final Producto p){
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle(actividad.getString(R.string.titulo_navigation_drawer));
        final String[] ops = getResources().getStringArray(R.array.opciones_linea);
        builder.setItems(ops, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    listaProductos.remove(p);
                    cargarListaProductos();
                } else if (which == 1) {
                    productoModificar = listaProductos.indexOf(p);
                    actividad.mostrarFragmentoNuevaLinea(true, p);
                }
            }
        });
        builder.show();
    }

    private void cambioFechaVencimiento(int position){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = formatoFecha.parse(etFechaFactura.getText().toString());
            cal.setTime(date);
        } catch (ParseException ignored) {
        }
        cal.setTime(date);
        int dias = 0;
        switch (position){
            case 0:
                break;
            case 1:
                dias = 8;
                break;
            case 2:
                dias = 14;
                break;
            case 3:
                dias = 30;
                break;
            case 4:
                dias = 0;
                break;
        }
        if(position != 0) {
            cal.add(Calendar.DAY_OF_MONTH, dias);
            etFechaVenciminetoFactura.setText(formatearFecha(cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.YEAR)));
        }
    }

    private void cargarListaProductos(){
        llLineas.removeAllViewsInLayout();
        llLineas.invalidate();
        if(getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.nueva_factura_tv_sin_producto);
            if (listaProductos.size() > 0) {
                tv.setVisibility(View.GONE);
                for (Producto producto : listaProductos) {
                    crearViewLinea(producto);
                }
            } else {
                tv.setVisibility(View.VISIBLE);
            }
        }
        cargarTotales();
    }

    private void cargarTotales(){
        Hashtable<Double, Double> tablaIva = new Hashtable<>();
        double subtotal = 0.0;
        double total = 0.0;
        if(listaProductos.size()>0){
            for(Producto producto : listaProductos){
                double precio_producto_sin_iva = Metodos.stringToDouble(producto.getPrecio_venta());
                double iva_producto_porcentaje = Metodos.stringToDouble(producto.getTipo_iva());
                double precio_producto_iva_incluido = precio_producto_sin_iva * (1+iva_producto_porcentaje/100);
                if(!tablaIva.containsKey(iva_producto_porcentaje)){
                    tablaIva.put(iva_producto_porcentaje, Metodos.redondear(precio_producto_sin_iva * Metodos.stringToDouble(producto.getCantidad()), 2));
                } else {
                    double temp = tablaIva.get(iva_producto_porcentaje);
                    tablaIva.put(iva_producto_porcentaje, Metodos.redondear(temp + precio_producto_sin_iva * Metodos.stringToDouble(producto.getCantidad()),2));
                }
                subtotal = Metodos.redondear(subtotal + precio_producto_sin_iva * Metodos.stringToDouble(producto.getCantidad()),2);
                total = Metodos.redondear(total + precio_producto_iva_incluido*Metodos.stringToDouble(producto.getCantidad()),2);
            }
        }
        if(tablaIva.size()>0){
            cargarTotalesIva(tablaIva);
        }
        tvTotal.setText(Metodos.doubleToMoney(total));
        tvSubtotal.setText(Metodos.doubleToMoney(subtotal));
    }

    private void cargarTotalesIva(Hashtable<Double, Double> tablaIva){
        llTotalesIva.removeAllViewsInLayout();
        llTotalesIva.invalidate();
        Enumeration<Double> keys = tablaIva.keys();
        while(keys.hasMoreElements()) {
            double key = keys.nextElement();
            double value = tablaIva.get(key);
            View detalle = View.inflate(actividad, R.layout.detalle_totales_iva, null);
            TextView tv_etiqueta_iva = (TextView)detalle.findViewById(R.id.detalle_totales_iva_etiqueta);
            TextView tv_total_iva = (TextView)detalle.findViewById(R.id.detalle_totales_iva_total);

            tv_etiqueta_iva.setTextAppearance(actividad, android.R.style.TextAppearance_DeviceDefault_Medium);
            tv_total_iva.setTextAppearance(actividad, android.R.style.TextAppearance_DeviceDefault_Medium);

            tv_etiqueta_iva.setText("IVA " + key + "% de " + Metodos.doubleToMoney(value));
            tv_total_iva.setText(Metodos.doubleToMoney(value*key/100));

            llTotalesIva.addView(detalle);
        }
    }

    private void cargarView(){
        cargarViewCliente();
        cargarViewInfoFactura();
        cargarViewLineasFactura();
        cargarViewOpciones();
        cargarViewTotales();
    }

    private void cargarViewCliente(){
        if(getView() != null) {
            etCliente = (EditText) getView().findViewById(R.id.nueva_factura_et_cliente);
            etCliente.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Fragment fragment = new FragmentoSeleccionarCliente();
                        NuevaFactura.fragmentoActual = NuevaFactura.ListaFragmentosNuevaFactura.seleccionCliente;
                        Bundle bundle = new Bundle();
                        bundle.putString("query", "");
                        fragment.setArguments(bundle);
                        FragmentoNuevaFactura.this.getActivity().invalidateOptionsMenu();
                        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.relativeLayoutFactura, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        getFragmentManager().executePendingTransactions();
                    }
                    return true;
                }
            });
        }
    }

    private void cargarViewInfoFactura(){
        if(getView() != null) {
            spCondiciones_pago = (Spinner) getView().findViewById(R.id.nueva_factura_sp_condiciones_pago);
            etFechaFactura = (EditText) getView().findViewById(R.id.nueva_factura_et_fecha_factura);
            etFechaVenciminetoFactura = (EditText) getView().findViewById(R.id.nueva_factura_et_fecha_vencimiento);
            etNotas = (EditText) getView().findViewById(R.id.nueva_factura_et_notas);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.lista_condiciones_pago_factura, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCondiciones_pago.setAdapter(adapter);

            final Calendar cal = Calendar.getInstance();
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), false);

            etFechaFactura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actividad.getSupportFragmentManager().findFragmentByTag(FECHA_FACTURA_TAG) == null &&
                            actividad.getSupportFragmentManager().findFragmentByTag(FECHA_VENCIMIENTO_TAG) == null) {
                        datePickerDialog.setVibrate(false);
                        datePickerDialog.setYearRange(1990, 2037);
                        datePickerDialog.show(actividad.getSupportFragmentManager(), FECHA_FACTURA_TAG);
                    }
                }
            });
            etFechaFactura.setText(formatearFecha(cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR)));

            spCondiciones_pago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cambioFechaVencimiento(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spCondiciones_pago.setSelection(2);
            cambioFechaVencimiento(2);

            etFechaVenciminetoFactura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actividad.getSupportFragmentManager().findFragmentByTag(FECHA_VENCIMIENTO_TAG) == null
                            && actividad.getSupportFragmentManager().findFragmentByTag(FECHA_FACTURA_TAG) == null) {
                        datePickerDialog.setVibrate(false);
                        datePickerDialog.setYearRange(1990, 2037);
                        datePickerDialog.show(actividad.getSupportFragmentManager(), FECHA_VENCIMIENTO_TAG);
                    }
                }
            });
        }
    }

    private void cargarViewLineasFactura(){
        if(getView() != null) {
            TextView et_nuevaLinea = (TextView) getView().findViewById(R.id.nueva_factura_bt_nueva_linea);
            Metodos.textViewAwesomeComponente(getActivity(), et_nuevaLinea, actividad.getString(R.string.icono_nueva_linea));
            LinearLayout llNuevaLinea = (LinearLayout) getView().findViewById(R.id.nueva_factura_layout_nueva_linea);
            llLineas = (LinearLayout) getView().findViewById(R.id.nueva_factura_layout_lineas);
            llNuevaLinea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productoModificar = -1;
                    actividad.mostrarFragmentoNuevaLinea(true, null);
                }
            });
        }
    }

    private void cargarViewOpciones(){
        if(getView() != null) {
            formatoNeto = (RadioButton) getView().findViewById(R.id.radioNeto);
            formatoNeto.setChecked(true);
            formatoNeto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cargarListaProductos();
                }
            });
        }
    }

    private void cargarViewTotales(){
        if(getView() != null) {
            tvSubtotal = (TextView) getView().findViewById(R.id.nueva_factura_tv_subtotal);
            tvTotal = (TextView) getView().findViewById(R.id.nueva_factura_tv_total);
            llTotalesIva = (LinearLayout) getView().findViewById(R.id.nueva_factura_layout_totales_iva);
        }
    }

    private Hashtable<String, String> crearParametros(){
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put(PARAMETRO_VENCIMIENTO, etFechaVenciminetoFactura.getText().toString());
        parametros.put(PARAMETRO_CLIENTE, actividad.getClienteFactura().getId()+"");
        parametros.put(PARAMETRO_NOTAS, "");
        parametros.put(PARAMETRO_FECHA, etFechaFactura.getText().toString());
        StringBuilder productos = new StringBuilder();
        productos.append("{");
        for (int i = 0; i < listaProductos.size() ; i++) {
            StringBuilder obj = new StringBuilder();
            if(i!=0){
                obj.append(",");
            }
            obj.append("[");
            obj.append((listaProductos.get(i).getId_a() + "").concat(","));
            obj.append(listaProductos.get(i).getCantidad());
            obj.append("]");
            productos.append(obj);
        }
        productos.append("}");
        parametros.put(PARAMETRO_PRODUCTOS, productos.toString());
        return parametros;
    }

    private void crearViewLinea(final Producto producto){
        View detalle = View.inflate(actividad, R.layout.detalle_linea_factura, null);
        TextView tv_cantidad_precio = (TextView)detalle.findViewById(R.id.detalle_linea_tv_cantidad_precio);
        TextView tv_producto = (TextView)detalle.findViewById(R.id.detalle_linea_tv_producto);
        TextView tv_precio = (TextView)detalle.findViewById(R.id.detalle_linea_tv_precio_total);
        tv_producto.setText(producto.getNombre_producto());
        double precio_producto_sin_iva = Metodos.stringToDouble(producto.getPrecio_venta());
        double iva_producto = Metodos.stringToDouble(producto.getTipo_iva())/100;
        double precio_producto_iva_incluido =  precio_producto_sin_iva * (iva_producto + 1);

        if(formatoNeto.isChecked()){
            tv_cantidad_precio.setText(producto.getCantidad() + " " + actividad.getString(R.string.nueva_factura_por) + Metodos.doubleToMoney(precio_producto_sin_iva));
            tv_precio.setText(Metodos.doubleToMoney(Metodos.stringToDouble(producto.getCantidad())*precio_producto_sin_iva));
        } else {
            tv_cantidad_precio.setText(producto.getCantidad() + " " + actividad.getString(R.string.nueva_factura_por) + Metodos.doubleToMoney(precio_producto_iva_incluido));
            tv_precio.setText(Metodos.doubleToMoney(Metodos.stringToDouble(producto.getCantidad())*precio_producto_iva_incluido));
        }
        detalle.setTag(producto);
        detalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Producto pos = (Producto) v.getTag();
                productoModificar = listaProductos.indexOf(pos);
                actividad.mostrarFragmentoNuevaLinea(true, pos);
            }
        });
        detalle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Producto p = (Producto) v.getTag();
                accionLinea(p);
                return true;
            }
        });
        llLineas.addView(detalle);
    }

    private String formatearFecha(int d, int m, int y){
        String dia = ("0" + d).substring(("0" + d).length()-2);
        String mes = ("0" + m).substring(("0" + m).length()-2);
        String anio = "" + y;
        return dia + "/" + mes + "/" + anio;
    }

    private void guardarFactura(){
        actividad.mostrarDialogo();
        Hashtable<String, String> parametros = crearParametros();
        AsyncTaskPost peticion = new AsyncTaskPost(actividad, this, Constantes.URL_POST_FACTURA, CODIGO_PETICION_FACTURA);
        peticion.execute(parametros);
    }

    public void setCliente(){
        etCliente.setText(actividad.getClienteFactura().getNombre_comercial());
    }

    public void setProducto(Producto producto){
        if(productoModificar == -1) {
            listaProductos.add(producto);
        } else {
            listaProductos.set(productoModificar, producto);
        }
        productoModificar = -1;
        cargarListaProductos();
    }

    /* *************************************************************************
     ************************* Interfaz OnDateSetListener **********************
     *************************************************************************** */

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if(datePickerDialog.getTag().equals(FECHA_FACTURA_TAG)) {
            etFechaFactura.setText(formatearFecha(day, month + 1, year));
            cambioFechaVencimiento(spCondiciones_pago.getSelectedItemPosition());
        } else if(datePickerDialog.getTag().equals(FECHA_VENCIMIENTO_TAG)){
            etFechaVenciminetoFactura.setText(day + "/" + (month + 1) + "/" + year);
            spCondiciones_pago.setSelection(0);
        }
    }

    /* *************************************************************************
     ******************** Interfaz OnProcessCompleteListener *******************
     *************************************************************************** */

    @Override
    public void resultadoPost(String respuesta, int codigo_peticion) {
        actividad.cerrarDialogo();
        if(respuesta != null) {
            if (respuesta.equals("r2")) {
                Metodos.redireccionarLogin(actividad);
            } else {
                switch (codigo_peticion) {
                    case CODIGO_PETICION_FACTURA:
                        if(!respuesta.contains("r")){
                            Metodos.tostada(actividad, actividad.getString(R.string.nueva_factura_insertada));
                            actividad.setResult(Activity.RESULT_OK);
                            actividad.finish();
                            actividad.overridePendingTransition(R.anim.fade_in, R.anim.slide_in_left);
                        } else {
                            Metodos.tostada(actividad, actividad.getString(R.string.nueva_factura_error_insertar));
                        }
                        break;
                }
            }
        } else{
            Metodos.tostada(actividad, actividad.getString(R.string.e_conexion));
        }
    }
}