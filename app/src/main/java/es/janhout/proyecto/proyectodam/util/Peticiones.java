package es.janhout.proyecto.proyectodam.util;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

public class Peticiones {

    public static String peticionGetJSON(Context contexto, String url, Hashtable<String, String> params){
        String linea;
        StringBuilder respuesta = new StringBuilder("");
        try {
            String parametros = crearParametros(params);
            URL u;
            if(parametros != null && !parametros.equals("")) {
                u = new URL(url + "?" + parametros);
            } else {
                u = new URL(url);
            }

            HttpURLConnection conexion = (HttpURLConnection) u.openConnection();
            conexion.setDoOutput(false);
            conexion.setInstanceFollowRedirects(false);
            conexion.setReadTimeout(15000);
            conexion.setConnectTimeout(5000);

            String cookieSesion = Metodos.leerPreferenciasCompartidasString(contexto, "cookieSesion");
            conexion.setRequestProperty("Cookie", cookieSesion);
            conexion.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            while ((linea = in.readLine()) != null) {
                respuesta.append(linea);
            }
            in.close();

            return new String(respuesta.toString().getBytes("UTF-8"), "UTF-8");
        }catch (IOException e){
            Log.e("error GET", e.toString());
            return null;
        }
    }

    public static String peticionGetFichero(Context contexto, String url, Hashtable<String, String> params){
        try {
            String parametros = crearParametros(params);
            URL u;
            if(parametros != null && !parametros.equals("")) {
                u = new URL(url + "?" + parametros);
            } else {
                u = new URL(url);
            }
            HttpURLConnection conexion = (HttpURLConnection) u.openConnection();
            conexion.setDoOutput(false);
            conexion.setInstanceFollowRedirects(false);
            conexion.setReadTimeout(15000);
            conexion.setConnectTimeout(5000);

            String cookieSesion = Metodos.leerPreferenciasCompartidasString(contexto, "cookieSesion");
            conexion.setRequestProperty("Cookie", cookieSesion);
            conexion.connect();

            String id = params.get("id");

            InputStream input = conexion.getInputStream();
            String fichero = contexto.getExternalFilesDir("") + "/" + id + ".tmp";
            OutputStream output = new FileOutputStream(fichero);

            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return fichero;
        } catch (Exception e) {
            Log.v("error GET fichero", e.toString());
            return null;
        }
    }

    public static String peticionPostJSON(Context contexto, String url, Hashtable<String, String> params) {
        try {
            String linea;
            StringBuilder resultado = new StringBuilder("");
            URL u = new URL(url);
            HttpURLConnection conexion = (HttpURLConnection)u.openConnection();

            conexion.setInstanceFollowRedirects(false);
            conexion.setReadTimeout(15000);
            conexion.setConnectTimeout(5000);

            String cookieSesion = Metodos.leerPreferenciasCompartidasString(contexto, "cookieSesion");

            if (cookieSesion != null && !cookieSesion.equals("")) {
                conexion.setRequestProperty("Cookie", cookieSesion);
            }

            String parametros = crearParametros(params);

            conexion.setRequestProperty("Content-size", Integer.toString(parametros.length()));

            conexion.setRequestMethod("POST");

            conexion.setDoInput(true);
            conexion.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(conexion.getOutputStream());
            out.write(parametros);
            out.flush();

            leerCookies(contexto, conexion);

            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            while ((linea = in.readLine()) != null) {
                resultado.append(linea);
            }
            in.close();

            return new String(resultado.toString().getBytes("UTF-8"), "UTF-8");
        }catch (IOException e){
            Log.v("error POST", e.toString());
            return null;
        }
    }

    public static String peticionPostFile(Context contexto, String url, String fichero) {
        try {
            String linea;
            StringBuilder resultado = new StringBuilder("");
            URL u = new URL(url);
            HttpURLConnection conexion = (HttpURLConnection)u.openConnection();

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(fichero);
            FileInputStream fileInputStream = new FileInputStream(sourceFile);

            conexion.setReadTimeout(15000);
            conexion.setConnectTimeout(5000);

            conexion.setDoInput(true);
            conexion.setDoOutput(true);
            conexion.setUseCaches(false);
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Connection", "Keep-Alive");
            conexion.setRequestProperty("ENCTYPE", "multipart/form-data");
            conexion.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conexion.setRequestProperty("fichero", fichero);
            String cookieSesion = Metodos.leerPreferenciasCompartidasString(contexto, "cookieSesion");

            if (cookieSesion != null && !cookieSesion.equals("")) {
                conexion.setRequestProperty("Cookie", cookieSesion);
            }

            DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fichero\";filename=\""+ fichero + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            while ((linea = in.readLine()) != null) {
                resultado.append(linea);
            }
            in.close();

            return new String(resultado.toString().getBytes("UTF-8"), "UTF-8");
        }catch (IOException e){
            Log.v("error POST", e.toString());
            return null;
        }
    }

    /* *************************************************************************
     ******************************* Auxiliares ********************************
     *************************************************************************** */

    private static String crearParametros(Hashtable<String, String> params){
        if(params == null || params.size() == 0) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        Enumeration<String> keys = params.keys();
        while(keys.hasMoreElements()) {
            resultado.append(resultado.length() == 0 ? "" : "&");
            String key = keys.nextElement();
            try {
                resultado.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                resultado.append(key).append("=").append(params.get(key));
            }
        }
        return resultado.toString();
    }

    private static void leerCookies(Context contexto, HttpURLConnection con) {
        StringBuilder cookieBuffer = null;
        String cabecera;
        String nombreCabecera;
        for (int i = 1; (nombreCabecera = con.getHeaderFieldKey(i)) != null; i++) {
            if (nombreCabecera.toLowerCase().equals("set-cookie")) {
                cabecera = con.getHeaderField(i);
                cabecera = cabecera.substring(0, cabecera.indexOf(";"));
                if (cookieBuffer != null && cabecera.contains("PHP")) {
                    cookieBuffer.append("; ");
                } else {
                    cookieBuffer = new StringBuilder();
                }
                cookieBuffer.append(cabecera);
            }
        }
        if (cookieBuffer != null) {
            Metodos.escribirPreferenciasCompartidasString(contexto, "cookieSesion", cookieBuffer.toString());
        }
    }
}