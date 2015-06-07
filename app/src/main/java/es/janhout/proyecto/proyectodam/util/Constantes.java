package es.janhout.proyecto.proyectodam.util;

public class Constantes {

    private Constantes() {
    }

    //private final static String PROTOCOLO = "https://";
    //private final static String URL_BASE = "proyectodam-rafaelgomez.rhcloud.com/";
    private final static String PROTOCOLO = "http://";
    private final static String URL_BASE = "192.168.1.105/servidor/";

    private final static String MI_URL = PROTOCOLO + URL_BASE;

    public final static String URL_LOGIN = MI_URL + "login.php";
    public final static String URL_RECUPERAR_PASS = MI_URL + "recuperarPass.php";

    private final static String URL_POST = MI_URL + "post/";
    private final static String URL_DELETE = MI_URL + "delete/";
    private final static String URL_GET = MI_URL + "get/";
    private final static String URL_SET = MI_URL + "set/";

    public final static String URL_GET_FACTURAS = URL_GET + "facturas.php";
    public final static String URL_GET_PRODUCTOS = URL_GET + "productos.php";
    public final static String URL_GET_CLIENTES = URL_GET + "clientes.php";
    public final static String URL_GET_LOCALIDADES = URL_GET + "localidades.php";
    public final static String URL_GET_PROVINCIAS = URL_GET + "provincias.php";
    public final static String URL_GET_GASTOS = URL_GET + "gastos.php";
    public final static String URL_GET_TIPO_DIRECCION = URL_GET + "tipoDireccion.php";
    public final static String URL_GET_PDF_FACTURA = URL_GET + "pdfFactura.php";
    public final static String URL_GET_FOTO_GASTO = URL_GET + "fotoGasto.php";
    public final static String URL_GET_COMPROBAR_CLIENTE = URL_GET + "comprobarCliente.php";
    public final static String URL_GET_COMPROBAR_PRODUCTO = URL_GET + "comprobarProducto.php";
    public final static String URL_GET_COMPROBAR_USUARIO = URL_GET + "comprobarUsuario.php";

    public final static String URL_DELETE_FACTURA = URL_DELETE + "factura.php";
    public final static String URL_DELETE_PRODUCTO = URL_DELETE + "producto.php";
    public final static String URL_DELETE_CLIENTE = URL_DELETE + "cliente.php";
    public final static String URL_DELETE_GASTO = URL_DELETE + "gasto.php";
    public final static String URL_DELETE_LOCALIDAD = URL_DELETE + "localidad.php";
    public final static String URL_DELETE_PROVINCIA = URL_DELETE + "provincia.php";
    public final static String URL_DELETE_TIPO_DIRECCION = URL_DELETE + "tipoDireccion.php";

    public final static String URL_POST_FACTURA = URL_POST + "factura.php";
    public final static String URL_POST_PRODUCTO = URL_POST + "producto.php";
    public final static String URL_POST_CLIENTE = URL_POST + "cliente.php";
    public final static String URL_POST_LOCALIDAD = URL_POST + "localidad.php";
    public final static String URL_POST_PROVINCIA = URL_POST + "provincia.php";
    public final static String URL_POST_TIPO_DIRECCION = URL_POST + "tipoDireccion.php";
    public final static String URL_POST_GASTO = URL_POST + "gasto.php";
    public final static String URL_POST_GASTO_FOTO = URL_POST +  "gastoFoto.php";
    public final static String URL_POST_USUARIO = URL_POST + "usuario.php";


    public static final String SET_FAVORITO = URL_SET + "setClienteFavorito.php";
}
