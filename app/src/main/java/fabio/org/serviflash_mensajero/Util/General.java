//package corp.app.com.serviflash_cliente.Util;
package fabio.org.serviflash_mensajero.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.ImagenesListView.ImageLoader;


/**
 * Created by fabio on 5/12/15.
 */
public class General {

    public static Context contexto;
    public Activity activity;
    private ProgressDialog dialogCargando;

    public General(Activity context){

        this.activity = context;
    }

    public General(Context context, View v){
        this.contexto = context;
        if(v != null){
            ImageLoader cargarImagen = new ImageLoader(context);
            Empleado cl = cargarCliente();
            ((TextView)v.findViewById(R.id.username)).setText(cl.getNombreape());
            ((TextView)v.findViewById(R.id.correo)).setText(cl.getPlacamoto());
            //final ImageView img = ((ImageView)v.findViewById(R.id.circle_image));
            //cargarImagen.DisplayImage(cl.getRuta(),img);
        }
    }

    public void mostrarDialog(String titulo, String mensaje, boolean cancelable){
        AlertDialog.Builder b = new AlertDialog.Builder(contexto);
        b.setTitle(titulo);
        b.setMessage(mensaje);
        b.setCancelable(cancelable);
        b.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog al = b.create();
        al.show();
    }

    public void initCargando(String mensaje){
        dialogCargando = new ProgressDialog(contexto);
        dialogCargando.setMessage(mensaje);
        dialogCargando.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogCargando.setCancelable(false);
        dialogCargando.show();
    }

    public void finishCargando(){
        dialogCargando.dismiss();
    }

    public void guardarCliente(Empleado c,boolean face){
        SharedPreferences prefs = contexto.getSharedPreferences("serviflash_mensajero", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id", c.getId());
        editor.putString("email", c.getCorreo());
        editor.putBoolean("cuenta", true);
        editor.putString("nombreape", c.getNombreape());
        editor.putString("ruta", c.getRuta());
        editor.putString("pass", c.getClave());
        editor.putString("placamoto", c.getPlacamoto());
        editor.putBoolean("face",face);
        editor.commit();
    }

    public int getIdCliente(){
        SharedPreferences prefs = contexto.getSharedPreferences("serviflash_mensajero", Context.MODE_PRIVATE);
        return prefs.getInt("id", 0);
    }

    public Empleado cargarCliente(){
        SharedPreferences prefs = contexto.getSharedPreferences("serviflash_mensajero", Context.MODE_PRIVATE);
        Empleado c = new Empleado();
        c.setId(prefs.getInt("id",0));
        c.setCorreo(prefs.getString("email", null));
        c.setNombreape(prefs.getString("nombreape", null));
        c.setRuta(prefs.getString("ruta",null));
        c.setPlacamoto(prefs.getString("placamoto",null));
        c.setClave(prefs.getString("pass", null));
        return c;
    }

    public void quitarCuenta(){
        System.out.println("cuenta false");
        SharedPreferences prefs = contexto.getSharedPreferences("serviflash_mensajero", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cuenta", false);
        editor.commit();
    }

    public boolean sesion(){
        SharedPreferences prefs = contexto.getSharedPreferences("serviflash_mensajero", Context.MODE_PRIVATE);
        return prefs.getBoolean("cuenta", false);
    }

    public float SpToPx(int sp) {
        return sp * contexto.getResources().getDisplayMetrics().scaledDensity;
    }


}
