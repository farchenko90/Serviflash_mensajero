package layout;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fabio.org.serviflash_mensajero.Adapter.AdaptadorPedidos;
import fabio.org.serviflash_mensajero.Modelos.Empleado;
import fabio.org.serviflash_mensajero.R;
import fabio.org.serviflash_mensajero.Services.Webservices;
import fabio.org.serviflash_mensajero.Util.General;

/**
 * A simple {@link Fragment} subclass.
 */
public class Lista_pedidos extends Fragment {


    General gn;
    View rootView;
    Activity myActivity;
    ListView listaPedidos;
    ImageView lblEstado;
    TextView texto;

    ArrayList<JSONObject> listaDatosPedidos;
    AdaptadorPedidos adaptadorPedidos;
    Context myContext;

    public Lista_pedidos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_lista_pedidos, container, false);

        gn = new General(myActivity,null);
        listaPedidos = (ListView) rootView.findViewById(R.id.lista);
        lblEstado = (ImageView) rootView.findViewById(R.id.lblEstado);
        texto = (TextView) rootView.findViewById(R.id.lblmensaje);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        cargarPedidos();
    }

    public void onResume(){
        super.onResume();
        cargarPedidos();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        cargarPedidos();
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myActivity = (AppCompatActivity) activity;
    }

    private void cargarPedidos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            final Webservices cs = new Webservices();
            final JSONArray j = cs.mispedidos(gn.getIdCliente());
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    listaDatosPedidos = new ArrayList<>();
                    if (j == null) {
                        Toast.makeText(myActivity, "No hay pedidos realizados", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < j.length(); i++) {
                            listaDatosPedidos.add(j.getJSONObject(i));
                        }
                    }
                    adaptadorPedidos = new AdaptadorPedidos(myActivity, listaDatosPedidos);
                    listaPedidos.setAdapter(adaptadorPedidos);
                    if(listaDatosPedidos.size() == 0){
                        lblEstado.setVisibility(View.VISIBLE);
                        texto.setVisibility(View.VISIBLE);
                    }else{
                        lblEstado.setVisibility(View.GONE);
                        texto.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    //Toast.makeText(myActivity, "Error webSerice Principal, " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                    }
                });
            }
        }).start();
    }

    public void onDestroy(){
        super.onDestroy();
    }

}
