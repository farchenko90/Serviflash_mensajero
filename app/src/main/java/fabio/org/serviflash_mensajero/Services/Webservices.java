package fabio.org.serviflash_mensajero.Services;

import android.os.StrictMode;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import fabio.org.serviflash_mensajero.Modelos.Empleado;

/**
 * Created by root on 20/08/16.
 */
public class Webservices {

    public String msgError;
    public String mensaje;
    /*
    * Inicio sesion de empleado
    */
    public JSONObject login(Empleado c)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"autenticarmensajero");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user", c.getLogin());
            jsonParam.put("clave", c.getClave());
            System.out.println("JSON: "+jsonParam);
            //Log.i("PARAMETROS: ",jsonParam.toString());

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject pagoempleado(Double valor, int idpedido, String cliente)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"pago");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("valor", valor);
            jsonParam.put("idpedido",idpedido);
            jsonParam.put("recibe",cliente);
            System.out.println("JSON: "+jsonParam);
            //Log.i("PARAMETROS: ",jsonParam.toString());

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONArray mispedidos(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"mensajero/"+id+"/pedido");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONArray json = new JSONArray(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONArray mispedidosencamino(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"mensajero/"+id+"/encamino");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONArray json = new JSONArray(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject ubicacionmapa(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"mostrar/pedidos/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject mensajero(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"empleado/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject consultaestado(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"consulta/estado/empleado/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject actualizarposicion(int id,String latitud,String longitud)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"api/ubicaccion/"+id+"/"+latitud+"/"+longitud);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject estadopedido(String estado,int idpedido)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"notificacion/cliente/"+idpedido);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("estado", estado);
            System.out.println("JSON: "+jsonParam);
            //Log.i("PARAMETROS: ",jsonParam.toString());

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject estadoempleado(String estado,int idempleado)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"ubication/"+idempleado);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("estado", estado);
            System.out.println("JSON: "+jsonParam);
            //Log.i("PARAMETROS: ",jsonParam.toString());

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);
                    System.out.println("404: "+json);
                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);
                    System.out.println("200: "+json);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject updatepush(String idpush,int id){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"push/mensajero/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("idpush", idpush);
            System.out.println(jsonParam);

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch (Exception ex){
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject CambioEstado(int id,String estado){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"estado/pedido/"+id);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("estado", estado);
            System.out.println(jsonParam);

            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            //printout.writeUTF(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            byte[] buf = jsonParam.toString().getBytes("UTF-8");
            printout.write(buf);
            printout.flush ();
            printout.close ();
            BufferedReader rd;
            String jsonText;
            JSONObject json;
            switch (connection.getResponseCode()){
                case 404:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;
                case 200:
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    jsonText = readAll(rd);
                    json = new JSONObject(jsonText);

                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch (Exception ex){
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }

    public JSONObject mensajeroposicion(int id)
    {
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"mensajero/"+id+"/ubicacion");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch(Exception ex)
        {
            System.out.println("excepcion: "+ex);
            mensaje = null;
        }
        return null;
    }

    public JSONObject Actualizarposicion(int id,String lat,String lon,String hora)
    {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(Server.ruta+"ubicaccion/"+id+"/"+lat+"/"+lon+"/"+hora);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            switch (connection.getResponseCode()){
                case 404:
                    mensaje = "Datos no encontrados";
                    System.out.println(mensaje);
                    return null;
                case 200:
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONObject json = new JSONObject(jsonText);
                    connection.disconnect();
                    return json;

                default:
                    mensaje = "Error interno en el servidor";
                    return null;
            }

        }catch (Exception ex){
            msgError = "Error: "+ex.getMessage();
        }
        return null;
    }



    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
